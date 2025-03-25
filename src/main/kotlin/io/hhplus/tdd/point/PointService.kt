package io.hhplus.tdd.point

import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.command.ChargePoint
import io.hhplus.tdd.point.command.PointAmount
import org.springframework.stereotype.Service

@Service
class PointService(
    private val table: UserPointTable,
) {
    fun handle(command: ChargePoint): UserPoint {
        val userPoint = table.selectById(command.userId)
        val totalAmount = (command.amount.value + userPoint.point).also { it.verifyAmount() }
        return table.insertOrUpdate(command.userId, totalAmount)
    }

    fun getPoint(id: Long): UserPoint =
        table.selectById(id)

    private fun Long.verifyAmount(): PointAmount = PointAmount(this)

}
