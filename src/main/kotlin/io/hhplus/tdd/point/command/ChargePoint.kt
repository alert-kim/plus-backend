package io.hhplus.tdd.point.command

data class ChargePoint (
    val userId: Long,
    val amount: PointAmount,
)
