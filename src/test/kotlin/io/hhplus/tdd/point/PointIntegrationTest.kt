package io.hhplus.tdd.point

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.command.PointAmount
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PointIntegrationTest @Autowired constructor(
    private val controller: PointController,
    private val userPointTable: UserPointTable,
    private val historyTable: PointHistoryTable,
) {
    @Nested
    @DisplayName("포인트 조회")
    inner class GetPointTest {
        @Test
        fun `저장된 포인트 조회`() {
            val userId = 1L
            val pointAmount = 10L
            val savedPoint = userPointTable.insertOrUpdate(userId, pointAmount)

            val result = controller.point(userId)

            assertThat(result).isEqualTo(savedPoint)
        }

        @Test
        fun `포인트가 저장되지 않았던 유저는 0포인트`() {
            val id = 1L

            val result = controller.point(id)

            assertAll(
                { assertThat(result.id).isEqualTo(id) },
                { assertThat(result.point).isEqualTo(0) }
            )
        }
    }

    @Nested
    @DisplayName("포인트 내역 조회")
    inner class GetHistoryTest {
        @Test
        fun `해당 유저의 저장된 포인트 내역 조회`() {
            val userId = 1L
            val histories = List(3) {
                val history = PointMock.pointHistory(
                    userId = userId,
                )
                val saved = historyTable.insert(
                    userId = history.userId,
                    amount = history.amount,
                    transactionType = history.type,
                    updateMillis = history.timeMillis,
                )
                saved
            }
            val otherUserId = 2L
            historyTable.insert(otherUserId, 10L, TransactionType.CHARGE, 1000L)

            val result = controller.history(userId)

            assertThat(result).isEqualTo(histories)
        }

        @Test
        fun `히스토리가 없는 유저의 응답값은 빈 리스트`() {
            val userId = 1L

            val result = controller.history(userId)

            assertThat(result).isEmpty()
        }
    }

    @Nested
    @DisplayName("포인트 충전")
    inner class ChargePointTest {
        @Test
        fun `포인트가 저장된 유저가 포인트 충전시 포인트가 합산됨`() {
            val userId = 1L
            val amount = 100L
            userPointTable.insertOrUpdate(userId, amount)

            val amountToAdd = 200L
            val result = controller.charge(id = userId, amountToAdd)

            assertAll(
                { assertThat(result.id).isEqualTo(userId) },
                { assertThat(result.point).isEqualTo(amount + amountToAdd) },
            )
        }

        @Test
        fun `포인트를 충전하면 포인트 내역이 저장됨`() {
            val userId = 1L
            val amount = 100L

            controller.charge(id = userId, amount)

            val histories = historyTable.selectAllByUserId(userId)
            assertAll(
                { assertThat(histories).hasSize(1) },
                { assertThat(histories[0].userId).isEqualTo(userId) },
                { assertThat(histories[0].amount).isEqualTo(amount) },
                { assertThat(histories[0].type).isEqualTo(TransactionType.CHARGE) },
            )
        }

        @Test
        fun `포인트가 없던 유저가 포인트 충전시 해당 포인트만큼 충전`() {
            val userId = 1L
            val amountToAdd = 100L

            val result = controller.charge(userId, amountToAdd)

            assertAll(
                { assertThat(result.id).isEqualTo(userId) },
                { assertThat(result.point).isEqualTo(amountToAdd) },
            )
        }

        @Test
        fun `합산된 포인트가 포인트 최대값을 넘는 경우 IllegalArgumentException 발생하며, 포인트는 그대로 유지`() {
            val id = 1L
            userPointTable.insertOrUpdate(id, PointAmount.MAX_POINT)

            assertThrows<IllegalArgumentException> {
                controller.charge(id, 100L)
            }

            assertThat(userPointTable.selectById(id).point).isEqualTo(PointAmount.MAX_POINT)
        }
    }

    @Nested
    @DisplayName("포인트 사용")
    inner class UsePointTest {
        @Test
        fun `포인트가 저장된 유저가 포인트 사용시 포인트가 차감됨`() {
            val userId = 1L
            val amount = 500L
            userPointTable.insertOrUpdate(userId, amount)

            val amountToUse = 200L
            val result = controller.use(id = userId, amountToUse)

            assertAll(
                { assertThat(result.id).isEqualTo(userId) },
                { assertThat(result.point).isEqualTo(amount - amountToUse) },
            )
        }

        @Test
        fun `포인트를 사용하면 포인트 내역이 저장됨`() {
            val userId = 1L
            val amountToUse = 100L
            userPointTable.insertOrUpdate(userId, 100L)

            controller.use(id = userId, amountToUse)

            val histories = historyTable.selectAllByUserId(userId)
            assertAll(
                { assertThat(histories).hasSize(1) },
                { assertThat(histories[0].userId).isEqualTo(userId) },
                { assertThat(histories[0].amount).isEqualTo(amountToUse) },
                { assertThat(histories[0].type).isEqualTo(TransactionType.USE) },
            )
        }

        @Test
        fun `포인트가 없던 유저가 포인트 사용시 IllegalArgumentException 발생`() {
            val userId = 1L
            val amountToUser = 100L

            assertThrows<IllegalArgumentException> {
                controller.use(userId, amountToUser)
            }
        }

        @Test
        fun `사용 포인트가 보유 포인트보다 큰 경우 IllegalArgumentException 발생하며, 포인트는 그대로 유지`() {
            val userId = 1L
            val amount = 100L
            userPointTable.insertOrUpdate(userId, amount)

            assertThrows<IllegalArgumentException> {
                controller.use(userId, amount + 100L)
            }

            assertThat(userPointTable.selectById(userId).point).isEqualTo(amount)
        }
    }
}
