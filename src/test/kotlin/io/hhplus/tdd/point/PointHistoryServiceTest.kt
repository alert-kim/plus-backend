package io.hhplus.tdd.point

import io.hhplus.tdd.database.PointHistoryTable
import io.mockk.coEvery
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class PointHistoryServiceTest {
    @MockK
    private lateinit var table: PointHistoryTable

    @InjectMockKs
    private lateinit var service: PointHistoryService

    @Test
    fun `전달 받은 유저 id로 유저의 히스토리를 조회해 반환한다`() {
        val history = PointMock.pointHistory()
        val userHistories = listOf(history)
        val userId = history.userId
        coEvery { table.selectAllByUserId(userId) } returns userHistories

        val result = service.getAllByUser(userId)

        verify { table.selectAllByUserId(userId) }
        assertThat(result).isEqualTo(userHistories)
    }
}
