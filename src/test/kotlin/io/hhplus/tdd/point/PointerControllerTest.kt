package io.hhplus.tdd.point


import io.mockk.coEvery
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
    private lateinit var pointService: PointService
    @MockK
    private lateinit var historyService: PointHistoryService
    @InjectMockKs
    private lateinit var controller: PointController

    @Nested
    @DisplayName("유저 포인트 조회")
    inner class GetPointTest{
        @Test
        fun `전달 받은 id로 서비스에서 유저의 포인트를 조회해 반환한다`() {
            val userPoint = PointMock.userPoint()
            val id = userPoint.id
            coEvery { pointService.getPoint(id) } returns userPoint

            val result = controller.point(id)

            verify { pointService.getPoint(id) }
            assertThat(result).isEqualTo(userPoint)
        }
    }

    @Nested
    @DisplayName("포인트 내역 조회")
    inner class GetHistoryTest{
        @Test
        fun `전달 받은 유저 id로 서비스에서 유저의 포인트 내역을 조회해 반환한다`() {
            val history = PointMock.pointHistory()
            val userId = history.userId
            val userHistories = listOf(history)
            coEvery { historyService.getAllByUser(userId) } returns userHistories

            val result = controller.history(userId)

            verify { historyService.getAllByUser(userId) }
            assertThat(result).isEqualTo(userHistories)
        }
    }
}
