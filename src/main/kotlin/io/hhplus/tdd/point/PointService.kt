package io.hhplus.tdd.point

import io.hhplus.tdd.database.UserPointTable
import org.springframework.stereotype.Service

@Service
class PointService(
    private val table: UserPointTable,
) {
    fun getPoint(id: Long): UserPoint =
        table.selectById(id)
}
