package io.hhplus.tdd.point

import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.command.ChargePoint
import io.hhplus.tdd.point.command.PointAmount
import io.hhplus.tdd.point.command.UsePoint
import org.springframework.stereotype.Service

@Service
class PointService(
    private val table: UserPointTable,
    private val accessor: PointAccessor,
) {
    fun getPoint(id: Long): UserPoint =
        table.selectById(id)

    fun handle(command: ChargePoint): UserPoint =
        accessor.withLock(command.userId) {
            val userPoint = table.selectById(command.userId)
            val totalAmount = (userPoint.point + command.amount.value).also { it.verifyAmount() }
            table.insertOrUpdate(command.userId, totalAmount)
        }

    fun handle(command: UsePoint): UserPoint =
        accessor.withLock(command.userId) {
            val userPoint = table.selectById(command.userId)
            val totalAmount = (userPoint.point - command.amount.value).also { it.verifyAmount() }
            table.insertOrUpdate(command.userId, totalAmount)
        }

    private fun Long.verifyAmount(): PointAmount = PointAmount(this)
}
