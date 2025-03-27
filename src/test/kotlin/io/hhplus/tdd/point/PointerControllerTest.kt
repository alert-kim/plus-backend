package io.hhplus.tdd.point


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
class PointerControllerTest {
    @MockK
    private lateinit var pointTransactionManager: PointTransactionManager

    @MockK
    private lateinit var pointService: PointService

    @MockK
    private lateinit var historyService: PointHistoryService

    @InjectMockKs
    private lateinit var controller: PointController

    @Nested
    @DisplayName("유저 포인트 조회")
    inner class GetPointTest {
        @Test
        fun `전달 받은 id로 유저의 포인트를 조회해 반환한다`() {
            val userPoint = PointMock.userPoint()
            val id = userPoint.id
            every { pointService.getPoint(id) } returns userPoint

            val result = controller.point(id)

            verify { pointService.getPoint(id) }
            assertThat(result).isEqualTo(userPoint)
        }
    }

    @Nested
    @DisplayName("포인트 내역 조회")
    inner class GetHistoryTest {
        @Test
        fun `전달 받은 유저 id로 유저의 포인트 내역을 조회해 반환한다`() {
            val history = PointMock.pointHistory()
            val userId = history.userId
            val userHistories = listOf(history)
            every { historyService.getAllByUser(userId) } returns userHistories

            val result = controller.history(userId)

            verify { historyService.getAllByUser(userId) }
            assertThat(result).isEqualTo(userHistories)
        }
    }

    @Nested
    @DisplayName("포인트 충전")
    inner class ChargePointTest {
        @Test
        fun `전달 받은 id와 포인트로 해당 유저의 포인트를 충전해, 결과를 반환한다`() {
            val id = 1L
            val amount = 100L
            val userPoint = PointMock.userPoint()
            every { pointTransactionManager.charge(id, amount) } returns userPoint

            val result = controller.charge(id = id, amount)

            verify { pointTransactionManager.charge(id, amount) }
            assertThat(result).isEqualTo(userPoint)
        }
    }

    @Nested
    @DisplayName("포인트 사용")
    inner class UsePointTest {
        @Test
        fun `전달 받은 id와 포인트로 해당 유저의 포인트를 사용한 후, 결과를 반환한다`() {
            val id = 1L
            val amount = 100L
            val userPoint = PointMock.userPoint()
            every { pointTransactionManager.use(id, amount) } returns userPoint

            val result = controller.use(id = id, amount)

            verify { pointTransactionManager.use(id, amount) }
            assertThat(result).isEqualTo(userPoint)
        }
    }
}
