package stack.eliminationBackoffStack

import stack.Node
import stack.TreiberStack
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicReference

/**
 * A concurrent stack data structure that uses an elimination-backoff algorithm to resolve conflicts.
 *
 * @param T the type of elements in the stack
 */
class EliminationBackoffStack<T> : TreiberStack<T>() {
    private val top: AtomicReference<Node<T>?> = AtomicReference(null)
    private val eliminationArray: EliminationArray<T?> = EliminationArray(
        CAPACITY, TIMEOUT, UNIT
    )

    /**
     * Pushes an element onto the stack.
     *
     * @param item the element to push
     */
    override fun push(item: T) {
        val n = Node(item)
        while (true) {
            if (tryPush(n)) return
            try {
                eliminationArray.visit(item) ?: return
            } catch (_: TimeoutException) {
            }
        }
    }

    /**
     * Removes and returns the element at the top of the stack.
     *
     * @return the element at the top of the stack
     * @throws EmptyStackException if the stack is empty
     */
    @Throws(EmptyStackException::class)
    override fun pop(): T {
        while (true) {
            val n = tryPop()
            if (n != null) return n.item
            try {
                val y = eliminationArray.visit(null)
                if (y != null) return y
            } catch (_: TimeoutException) {
            }
        }
    }

    private fun tryPush(n: Node<T>): Boolean {
        val m = top.get()
        n.next = m
        return top.compareAndSet(m, n)
    }

    @Throws(EmptyStackException::class)
    private fun tryPop(): Node<T>? {
        val m = top.get() ?: throw EmptyStackException()

        val n = m.next
        return if (top.compareAndSet(m, n)) m else null
    }

    companion object {
        /**
         * The initial capacity of the internal array.
         */
        const val CAPACITY: Int = 100

        /**
         * The timeout value for the elimination-backoff algorithm.
         */
        const val TIMEOUT: Long = 1

        /**
         * The time unit for the timeout value.
         */
        val UNIT: TimeUnit = TimeUnit.MILLISECONDS
    }

    /**
     * Returns the element at the top of the stack without removing it.
     *
     * @return the element at the top of the stack, or `null` if the stack is empty
     */
}