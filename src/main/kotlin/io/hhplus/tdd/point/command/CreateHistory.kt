package io.hhplus.tdd.point.command

import io.hhplus.tdd.point.TransactionType

data class CreateHistory(
    val userId: Long,
    val amount: PointAmount,
    val transactionType: TransactionType,
    val updateMillis: Long,
)
