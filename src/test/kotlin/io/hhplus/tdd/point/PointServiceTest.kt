package io.hhplus.tdd.point

import io.hhplus.tdd.database.UserPointTable
import io.mockk.coEvery
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class PointServiceTest {
    @MockK
    private lateinit var table: UserPointTable
    @InjectMockKs
    private lateinit var service: PointService

    @Test
    fun `전달 받은 id로 테이블에서 유저의 포인트를 조회해 반환한다`() {
        val id = (1L..10L).random()
        val userPoint = UserPoint(id = id, point = (0L..10L).random(), updateMillis = System.currentTimeMillis())
        coEvery { service.getPoint(id) } returns userPoint

        val result = service.getPoint(id)

        verify { table.selectById(id) }
        assertThat(result).isEqualTo(userPoint)
    }
}
