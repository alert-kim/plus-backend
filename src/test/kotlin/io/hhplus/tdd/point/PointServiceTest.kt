package io.hhplus.tdd.point

import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.command.ChargePoint
import io.hhplus.tdd.point.command.PointAmount
import io.mockk.coEvery
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import io.mockk.verifyOrder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class PointServiceTest {
    @MockK(relaxed = true)
    private lateinit var table: UserPointTable

    @InjectMockKs
    private lateinit var service: PointService

    @Nested
    @DisplayName("포인트 조회")
    inner class GetPointTest {
        @Test
        fun `전달 받은 id로 유저의 포인트를 조회해 반환한다`() {
            val id = (1L..10L).random()
            val userPoint = UserPoint(id = id, point = (0L..10L).random(), updateMillis = System.currentTimeMillis())
            coEvery { service.getPoint(id) } returns userPoint

            val result = service.getPoint(id)

            verify { table.selectById(id) }
            assertThat(result).isEqualTo(userPoint)
        }
    }

    @Nested
    @DisplayName("포인트 충전")
    inner class ChargePointTest {
        @Test
        fun `전달 받은 id로 유저의 포인트를 조회해 포인트를 합산해 저장한다`() {
            val userPoint = PointMock.userPoint(point = 10L)
            val id = userPoint.id
            val command = ChargePoint(id, PointAmount(20L))
            val expectPoint = 30L
            coEvery { service.getPoint(id) } returns userPoint

            service.handle(command)

            verify {
                table.selectById(id)
                table.insertOrUpdate(id, expectPoint)
            }
        }

        @Test
        fun `반환된 포인트는 충전된 결과이다`() {
            val userPoint = PointMock.userPoint()
            val id = userPoint.id
            val command = ChargePoint(id, PointAmount(20L))
            val returnPoint = PointMock.userPoint()
            coEvery { service.getPoint(id) } returns userPoint
            coEvery { table.insertOrUpdate(id, any()) } returns returnPoint

            val result = service.handle(command)

            assertThat(result).isEqualTo(returnPoint)
        }

        @Test
        fun `합산된 포인트가 최대 포인트를 초과할 경우 에러가 발생한다`() {
            val userPoint = PointMock.userPoint(point = PointAmount.MAX_POINT)
            val id = userPoint.id
            val command = ChargePoint(id, PointAmount(20L))
            coEvery { service.getPoint(id) } returns userPoint

            assertThrows<IllegalArgumentException> {
                service.handle(command)
            }
        }
    }
}
