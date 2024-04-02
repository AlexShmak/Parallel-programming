package stack.eliminationBackoffStack

import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicStampedReference

/**
 * A concurrent object that allows for asynchronous object exchange.
 *
 * @param T the type of object that can be exchanged
 */
class Exchanger<T> {
    private val slot: AtomicStampedReference<T?> = AtomicStampedReference(null, 0)

    /**
     * Exchanges the object with the specified value.
     * @param y the object to deposit into the exchange
     * @param timeout the maximum time to wait for an object, in units of [unit]
     * @param unit the time unit for the [timeout] argument
     * @return the object withdrawn from the exchange, or `null` if the timeout expired
     * @throws TimeoutException if the timeout expired before an object was available
     */
    @Throws(TimeoutException::class)
    fun exchange(y: T, timeout: Long, unit: TimeUnit): T? {
        val wMax = unit.toNanos(timeout) / 1000
        val stamp = IntArray(1)
        while (System.nanoTime() < wMax) {
            var x = slot[stamp]
            when (stamp[0]) {
                EMPTY -> if (addA(y)) {
                    while (System.nanoTime() < wMax)
                        if ((removeB().also { x = it }) != null) return x

                    throw TimeoutException()
                }

                WAITING -> if (addB(x, y)) return x
                BUSY -> {}
                else -> {}
            }
        }
        throw TimeoutException()
    }

    /**
     * Adds an object to the exchange, if possible.
     *
     * @param y the object to add to the exchange
     * @return `true` if the object was added to the exchange, or `false` if the object could not be added because another
     * thread is currently withdrawing the object
     */
    private fun addA(y: T): Boolean {
        return slot.compareAndSet(null, y, EMPTY, WAITING)
    }

    /**
     * Adds an object to the exchange, if possible.
     * @param x the expected value of the object currently in the exchange
     * @param y the object to add to the exchange
     * @return `true` if the object was added to the exchange, or `false` if the object could not be added because the
     * expected value of the object currently in the exchange does not match the actual value
     */
    private fun addB(x: T?, y: T): Boolean {
        return slot.compareAndSet(x, y, WAITING, BUSY)
    }

    /**
     * Removes an object from the exchange, if possible.
     *
     * @return the object removed from the exchange, or `null` if no object was available
     */
    private fun removeB(): T? {
        val stamp = IntArray(1)
        val x = slot[stamp]
        if (stamp[0] != BUSY) return null

        slot[null] = EMPTY
        return x
    }

    private companion object {
        private const val EMPTY = 0
        private const val WAITING = 1
        private const val BUSY = 2
    }
}