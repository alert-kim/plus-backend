package io.hhplus.tdd.point.command

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class PointAmountTest {
    @ParameterizedTest
    @ValueSource(longs = [0L, 2L])
    fun `0이상인 수는 포인트가 될 수 있다`(amount: Long) {
        assertDoesNotThrow { PointAmount(amount) }
    }

    @Test
    fun `0미만인 수로 포인트를 생성하려고 하면 IllegalArgumentException가 발생한다`() {
        val amount = (-10L..-1L).random()

        assertThrows<IllegalArgumentException> {
            PointAmount(amount)
        }
    }

    @Test
    fun `포인트 최대값보다 큰 수로 포인트를 생성하려고 하면 IllegalArgumentException가 발생한다`() {
        val amount = PointAmount.MAX_POINT + 1

        assertThrows<IllegalArgumentException> {
            PointAmount(amount)
        }
    }
}
