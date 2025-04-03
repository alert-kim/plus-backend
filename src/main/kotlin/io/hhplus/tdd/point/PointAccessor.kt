package io.hhplus.tdd.point

import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class PointAccessor {
    private val locks = ConcurrentHashMap<Long, Any>()

    fun <T> withLock(userId: Long, block: () -> T): T {
        val lock = locks.computeIfAbsent(userId) { Any() }
        synchronized(lock) {
            return block()
        }
    }
}
