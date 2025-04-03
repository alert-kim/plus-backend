package io.hhplus.tdd.point.command

data class UsePoint(
    val userId: Long,
    val amount: PointAmount,
)
