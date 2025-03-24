package io.hhplus.tdd.point

import io.hhplus.tdd.point.command.ChargePoint
import io.hhplus.tdd.point.command.CreateHistory
import io.hhplus.tdd.point.command.PointAmount
import org.springframework.stereotype.Service

@Service
class PointTransactionManager(
    private val pointService: PointService,
    private val historyService: PointHistoryService,
) {
    fun charge(userId: Long, amount: Long): UserPoint {
        val amount = PointAmount(amount)
        val userPoint = pointService.handle(ChargePoint(userId, amount))
        historyService.handle(
            CreateHistory(
                userId = userId,
                amount = amount,
                transactionType = TransactionType.CHARGE,
                updateMillis = userPoint.updateMillis,
            )
        )
        return userPoint
    }
}
