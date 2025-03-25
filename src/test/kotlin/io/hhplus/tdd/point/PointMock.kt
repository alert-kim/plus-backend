package io.hhplus.tdd.point

import io.hhplus.tdd.point.command.PointAmount

object PointMock {
    fun userPoint(
        id: Long = (0L..10L).random(),
        point: Long = (0..10L).random(),
        updateMillis: Long = System.currentTimeMillis()
    ): UserPoint = UserPoint(id, point, updateMillis)

    fun pointAmount(
        value: Long = (0..10L).random()
    ): PointAmount = PointAmount(value)

    fun pointHistory(
        id: Long = (0L..10L).random(),
        userId: Long = (1L..10L).random(),
        type: TransactionType = TransactionType.USE,
        point: Long = (0..10L).random(),
        updateMillis: Long = System.currentTimeMillis()
    ): PointHistory = PointHistory(id, userId, type, point, updateMillis)
}
