package io.hhplus.tdd.point

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.database.UserPointTable
import org.springframework.stereotype.Service

@Service
class PointHistoryService(
    private val table: PointHistoryTable,
) {
    fun getAllByUser(userId: Long): List<PointHistory> =
        table.selectAllByUserId(userId)
}
