package io.hhplus.tdd.point

import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.command.ChargePoint
import io.hhplus.tdd.point.command.PointAmount
import io.hhplus.tdd.point.command.UsePoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PointConcurrencyTest @Autowired constructor(
    private val pointService: PointService,
    private val pointTable: UserPointTable,
) {

    @Test
    fun `같은 ID에 대해 동시에 충전을 할 경우 모두 성공한다`() {
        val userId = 1L
        val tryCount = 100
        val amountToCharge = 10L

        runBlocking {
            List(tryCount) {
                launch(Dispatchers.IO) { pointService.handle(ChargePoint(userId, PointAmount(amountToCharge))) }
            }.joinAll()
        }

        assertThat(pointTable.selectById(userId).point).isEqualTo(amountToCharge * tryCount)
    }

    @Test
    fun `같은 ID에 대해 동시에 사용을 할 경우 모두 성공한다`() {
        val userId = 1L
        val initialAmount = 1000L
        val tryCount = 100
        val amountToUse = 10L
        pointTable.insertOrUpdate(userId, initialAmount)

        val executor = Executors.newFixedThreadPool(10)
        val futures = mutableListOf<CompletableFuture<UserPoint>>()
        repeat(tryCount) {
            futures.add(
                CompletableFuture.supplyAsync({
                    pointService.handle(UsePoint(userId, PointAmount(amountToUse)))
                }, executor)
            )
        }
        CompletableFuture.allOf(*futures.toTypedArray()).join()

        val expect = initialAmount - (amountToUse * tryCount)
        assertThat(pointTable.selectById(userId).point).isEqualTo(expect)

        executor.shutdown()
    }

    @Test
    fun `같은 ID에 대해 동시에 충전과 사용을 할 경우 모두 성공한다`() {
        val userId = 1L
        val initialAmount = 1000L
        val chargeTryCount = 100
        val amountToCharge = 10L
        val useTryCount = 100
        val amountToUse = 10L
        pointTable.insertOrUpdate(userId, initialAmount)

        val countDownLatch = CountDownLatch(chargeTryCount + useTryCount)
        val executor = Executors.newFixedThreadPool(10)
        repeat(chargeTryCount) {
            executor.execute {
                pointService.handle(ChargePoint(userId, PointAmount(amountToCharge)))
                countDownLatch.countDown()
            }
        }
        repeat(useTryCount) {
            executor.execute {
                pointService.handle(UsePoint(userId, PointAmount(amountToUse)))
                countDownLatch.countDown()
            }
        }
        countDownLatch.await()

        val expect = initialAmount + (amountToCharge * chargeTryCount) - (amountToUse * useTryCount)
        assertThat(pointTable.selectById(userId).point).isEqualTo(expect)

        executor.shutdown()
    }
}

