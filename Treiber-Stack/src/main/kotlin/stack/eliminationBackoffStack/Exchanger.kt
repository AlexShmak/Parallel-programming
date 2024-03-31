package stack.eliminationBackoffStack

import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicStampedReference

// Exchanger is a lock-free object that permits two threads
// to exchange values, within a time limit.
internal class Exchanger<T> {
private    var slot: AtomicStampedReference<T?> = AtomicStampedReference(null, 0)


    @Throws(TimeoutException::class)
    fun exchange(y: T, timeout: Long, unit: TimeUnit): T? {
        val wMax = unit.toNanos(timeout)/1000// 1
//        val wMax = System.nanoTime() // 1
        val stamp = intArrayOf(EMPTY)
        while (System.nanoTime() < wMax) { // 2
            var x = slot[stamp] // 3
            when (stamp[0]) {
                EMPTY -> if (addA(y)) { // 4
                    while (System.nanoTime() < wMax) // 4
                        if ((removeB().also { x = it }) != null) return x // 4

                    throw TimeoutException() // 5
                }

                WAITING -> if (addB(x, y)) // 7
                    return x // 7

                BUSY -> {}
                else -> {}
            }
        }
        throw TimeoutException() // 2
    }


    private fun addA(y: T): Boolean { // 1, 2
        return slot.compareAndSet(null, y, EMPTY, WAITING)
    }


    private fun addB(x: T?, y: T): Boolean { // 1, 2
        return slot.compareAndSet(x, y, WAITING, BUSY)
    }


    private fun removeB(): T? {
        val stamp = intArrayOf(EMPTY)
        val x = slot[stamp] // 1
        if (stamp[0] != BUSY) return null // 1

        slot[null] = EMPTY // 2
        return x // 2
    }

    companion object {
        const val EMPTY: Int = 0
        const val WAITING: Int = 1
        const val BUSY: Int = 2
    }
}