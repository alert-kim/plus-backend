package io.hhplus.tdd.point

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.point.command.CreateHistory
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
class PointHistoryServiceTest {
    @MockK(relaxed = true)
    private lateinit var table: PointHistoryTable

    @InjectMockKs
    private lateinit var service: PointHistoryService

    @Nested
    @DisplayName("포인트 내역 조회")
    inner class GetPointTest {
        @Test
        fun `전달 받은 id를 가진 유저의 포인트 내역를 조회해 반환한다`() {
            val history = PointMock.pointHistory()
            val userHistories = listOf(history)
            val userId = history.userId
            every { table.selectAllByUserId(userId) } returns userHistories

            val result = service.getAllByUser(userId)

            verify { table.selectAllByUserId(userId) }
            assertThat(result).isEqualTo(userHistories)
        }
    }

    @Nested
    @DisplayName("포인트 내역 생성")
    inner class CreateHistoryTest {
        @Test
        fun `전달 받은 유저 id로 유저의 히스토리를 조회해 반환한다`() {
            val history = PointMock.pointHistory()
            val userId = history.userId
            val command = CreateHistory(
                userId = userId,
                amount = PointMock.pointAmount(),
                transactionType = TransactionType.entries.random(),
                updateMillis = System.currentTimeMillis()
            )

            service.handle(command)

            verify { table.insert(command.userId, command.amount.value, command.transactionType, command.updateMillis) }
        }
    }
}
