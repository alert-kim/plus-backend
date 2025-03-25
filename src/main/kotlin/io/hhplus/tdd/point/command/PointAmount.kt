package io.hhplus.tdd.point.command

@JvmInline
value class PointAmount (
    val value: Long,
) {
    init {
        require(value in 0..MAX_POINT) { "point amount must be in range 0 to $MAX_POINT" }
    }

    companion object {
        const val MAX_POINT = 10_000L
    }
}

