package io.hhplus.tdd.point

import io.hhplus.tdd.point.command.ChargePoint
import io.hhplus.tdd.point.command.CreateHistory
import io.hhplus.tdd.point.command.PointAmount
import io.hhplus.tdd.point.command.UsePoint
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class PointTransactionMangerTest {
    @MockK(relaxed = true)
    private lateinit var pointService: PointService

    @MockK(relaxed = true)
    private lateinit var historyService: PointHistoryService

    @InjectMockKs
    private lateinit var manager: PointTransactionManager

    @Nested
    @DisplayName("포인트 충전을 하면")
    inner class ChargeTest {
        @Test
        fun `해당하는 유저 아이디에 대한 포인트를 충전한다`() {
            val userId = (1..10).random().toLong()
            val amount = (100..200).random().toLong()
            manager.charge(userId, amount)

            verify { pointService.handle(ChargePoint(userId, PointAmount(amount))) }
        }

        @Test
        fun `해당하는 충전 내역을 생성한다`() {
            val userId = (1..10).random().toLong()
            val amount = (100..200).random().toLong()
            val returnPoint = PointMock.userPoint(id = userId)
            every { pointService.handle(ofType(ChargePoint::class)) } returns returnPoint

            manager.charge(userId, amount)

            verify {
                historyService.handle(
                    withArg<CreateHistory> {
                        assertThat(it.userId).isEqualTo(userId)
                        assertThat(it.amount).isEqualTo(PointAmount(amount))
                        assertThat(it.transactionType).isEqualTo(TransactionType.CHARGE)
                        assertThat(it.updateMillis).isEqualTo(returnPoint.updateMillis)
                    }
                )
            }
        }

        @Test
        fun `충전된 포인트가 반환된다`() {
            val returnPoint = PointMock.userPoint()
            every { pointService.handle(ofType(ChargePoint::class)) } returns returnPoint

            val result = manager.charge(1L, 100L)

            assertThat(result).isEqualTo(returnPoint)
        }
    }

    @Nested
    @DisplayName("포인트 사용을 하면")
    inner class UseTest {
        @Test
        fun `해당하는 유저 아이디에 대한 포인트를 사용한다`() {
            val userId = (1..10).random().toLong()
            val amount = (100..200).random().toLong()

            manager.use(userId, amount)

            verify { pointService.handle(UsePoint(userId, PointAmount(amount))) }
        }

        @Test
        fun `해당하는 사용 내역을 생성한다`() {
            val userId = (1..10).random().toLong()
            val amount = (100..200).random().toLong()
            val returnPoint = PointMock.userPoint(id = userId)
            every { pointService.handle(ofType(UsePoint::class)) } returns returnPoint

            manager.use(userId, amount)

            verify {
                historyService.handle(
                    withArg<CreateHistory> {
                        assertThat(it.userId).isEqualTo(userId)
                        assertThat(it.amount).isEqualTo(PointAmount(amount))
                        assertThat(it.transactionType).isEqualTo(TransactionType.USE)
                        assertThat(it.updateMillis).isEqualTo(returnPoint.updateMillis)
                    }
                )
            }
        }

        @Test
        fun `사용 후 포인트가 반환된다`() {
            val returnPoint = PointMock.userPoint()
            every { pointService.handle(ofType(UsePoint::class)) } returns returnPoint

            val result = manager.use(1L, 100L)

            assertThat(result).isEqualTo(returnPoint)
        }
    }
}
