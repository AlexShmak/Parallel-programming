package stack.eliminationBackoffStack

import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


// Elimination array provides a list of exchangers which
// are picked at random for a given value.
internal class EliminationArray<T>(capacity: Int, timeout: Long, unit: TimeUnit) {
    private var exchangers: Array<Exchanger<T>?> = arrayOfNulls<Exchanger<T>>(capacity)
    private val timeout: Long
    private val unit: TimeUnit
    private var random: Random

    // exchangers: array of exchangers
    // TIMEOUT: exchange timeout number
    // UNIT: exchange timeout unit
    // random: random number generator
    init {
        for (i in 0 until capacity) exchangers[i] = Exchanger()
        random = Random()
        this.timeout = timeout
        this.unit = unit
    }

    // 1. Try exchanging value on a random exchanger.
    @Throws(TimeoutException::class)
    fun visit(item: T): T? {
        val i = random.nextInt(exchangers.size)
        return exchangers[i]?.exchange(item, timeout, unit)
    }
}
