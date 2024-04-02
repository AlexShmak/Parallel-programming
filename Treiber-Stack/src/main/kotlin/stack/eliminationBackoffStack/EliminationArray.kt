package stack.eliminationBackoffStack

import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException



/**
 * A concurrent array that uses the elimination-backoff algorithm to provide
 * concurrent read/write access to an array of objects. 
 * @param T the type of object stored in the array
 * @param capacity the capacity of the array
 * @param timeout the timeout period for exchanges
 * @param unit the time unit for the timeout period
 */
internal class EliminationArray<T>(capacity: Int, timeout: Long, unit: TimeUnit) {
    private var exchangers: Array<Exchanger<T>?> = arrayOfNulls<Exchanger<T>>(capacity)
    private val timeout: Long
    private val unit: TimeUnit
    private var random: Random


    init {
        for (i in 0 until capacity) exchangers[i] = Exchanger()
        random = Random()
        this.timeout = timeout
        this.unit = unit
    }

    /**
     * Visits an element in the array, attempting to read or write it concurrently.
     *
     * @param item the element to visit
     * @return the element that was read or written, or null if the visit timed out
     * @throws TimeoutException if the visit timed out
     */
    @Throws(TimeoutException::class)
    fun visit(item: T): T? {
        val i = random.nextInt(exchangers.size)
        return exchangers[i]?.exchange(item, timeout, unit)
    }
}
