package io.hhplus.tdd.point


import io.mockk.coEvery
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class PointerControllerTest {
    @MockK
    private lateinit var pointService: PointService
    @InjectMockKs
    private lateinit var controller: PointController

    @Test
    fun `유저 포인트 조회 - 전달 받은 id로 서비스에서 유저의 포인트를 조회해 반환한다`() {
        val id = (1L..10L).random()
        val userPoint = UserPoint(id = id, point = (0L..10L).random(), updateMillis = System.currentTimeMillis())
        coEvery { pointService.getPoint(id) } returns userPoint

        val result = controller.point(id)

        verify { pointService.getPoint(id) }
        assertThat(result).isEqualTo(userPoint)
    }
}
