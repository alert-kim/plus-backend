package io.hhplus.tdd.point

import io.hhplus.tdd.point.command.ChargePoint
import io.hhplus.tdd.point.command.CreateHistory
import io.hhplus.tdd.point.command.PointAmount
import io.hhplus.tdd.point.command.UsePoint
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

    fun use(userId: Long, amount: Long): UserPoint {
        val amount = PointAmount(amount)
        val userPoint = pointService.handle(UsePoint(userId, amount))
        historyService.handle(
            CreateHistory(
                userId = userId,
                amount = amount,
                transactionType = TransactionType.USE,
                updateMillis = userPoint.updateMillis,
            )
        )
        return userPoint
    }
}
