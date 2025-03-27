package io.hhplus.tdd.point

import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.command.ChargePoint
import io.hhplus.tdd.point.command.PointAmount
import io.hhplus.tdd.point.command.UsePoint
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class PointServiceTest {
    @MockK(relaxed = true)
    private lateinit var table: UserPointTable

    @MockK(relaxed = true)
    private lateinit var accessor: PointAccessor

    @InjectMockKs
    private lateinit var service: PointService

    @BeforeEach
    fun setUp() {
        every { accessor.withLock<UserPoint>(any<Long>(), any()) } coAnswers {
            secondArg<() -> UserPoint>().invoke()
        }
    }

    @Nested
    @DisplayName("포인트 조회")
    inner class GetPointTest {
        @Test
        fun `전달 받은 id로 유저의 포인트를 조회해 반환한다`() {
            val id = (1L..10L).random()
            val userPoint = UserPoint(id = id, point = (0L..10L).random(), updateMillis = System.currentTimeMillis())
            every { service.getPoint(id) } returns userPoint

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
            every { service.getPoint(id) } returns userPoint

            service.handle(command)

            verify {
                table.selectById(id)
                table.insertOrUpdate(id, expectPoint)
            }
        }

        @Test
        fun `반환된 포인트는 충전된 결과이다`() {
            val userPoint = PointMock.userPoint(10L)
            val id = userPoint.id
            val command = ChargePoint(id, PointAmount(20L))
            val returnPoint = PointMock.userPoint()
            every { service.getPoint(id) } returns userPoint
            every { table.insertOrUpdate(id, any()) } returns returnPoint

            val result = service.handle(command)

            assertThat(result).isEqualTo(returnPoint)
        }

        @Test
        fun `합산된 포인트가 최대 포인트를 초과할 경우 IllegalArgumentException가 발생한다`() {
            val userPoint = PointMock.userPoint(point = PointAmount.MAX_POINT)
            val id = userPoint.id
            val command = ChargePoint(id, PointAmount(20L))
            every { service.getPoint(id) } returns userPoint

            assertThrows<IllegalArgumentException> {
                service.handle(command)
            }
        }
    }

    @Nested
    @DisplayName("포인트 사용")
    inner class UsePointTest {
        @Test
        fun `전달 받은 id로 유저의 포인트를 조회해 포인트를 삭감해 저장한다`() {
            val userPoint = PointMock.userPoint(point = 30L)
            val id = userPoint.id
            val command = UsePoint(id, PointAmount(10L))
            val expectPoint = 20L
            every { service.getPoint(id) } returns userPoint

            service.handle(command)

            verify {
                table.selectById(id)
                table.insertOrUpdate(id, expectPoint)
            }
        }

        @Test
        fun `반환된 포인트는 차감된 결과이다`() {
            val userPoint = PointMock.userPoint(point = 20L)
            val id = userPoint.id
            val command = UsePoint(id, PointAmount(10L))
            val returnPoint = PointMock.userPoint()
            every { service.getPoint(id) } returns userPoint
            every { table.insertOrUpdate(id, any()) } returns returnPoint

            val result = service.handle(command)

            assertThat(result).isEqualTo(returnPoint)
        }

        @Test
        fun `차감 후의 포인트가 0보다 작을 경우 IllegalArgumentException가 발생한다`() {
            val userPoint = PointMock.userPoint(point = 10L)
            val id = userPoint.id
            val command = UsePoint(id, PointAmount(20L))
            every { service.getPoint(id) } returns userPoint

            assertThrows<IllegalArgumentException> {
                service.handle(command)
            }
        }
    }
}
