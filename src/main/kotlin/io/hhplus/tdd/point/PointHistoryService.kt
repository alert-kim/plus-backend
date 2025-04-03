package io.hhplus.tdd.point

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.point.command.CreateHistory
import org.springframework.stereotype.Service

@Service
class PointHistoryService(
    private val table: PointHistoryTable,
) {
    fun getAllByUser(userId: Long): List<PointHistory> =
        table.selectAllByUserId(userId)

    fun handle(command: CreateHistory) {
        table.insert(
            userId = command.userId,
            amount = command.amount.value,
            transactionType = command.transactionType,
            updateMillis = command.updateMillis,
        )
    }
}
