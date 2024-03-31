package stack.eliminationBackoffStack

import stack.Node
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicReference

// Elimination-backoff stack is an unbounded lock-free LIFO
// linked list, that eliminates concurrent pairs of pushes
// and pops with exchanges.  It uses compare-and-set (CAS)
// atomic operation to provide concurrent access with
// obstruction freedom. In order to support even greater
// concurrency, in case a push/pop fails, it tries to
// pair it with another pop/push to eliminate the operation
// through exchange of values.
internal class EliminationBackoffStack<T> {
    private var top: AtomicReference<Node<T>?> = AtomicReference(null)
    private var eliminationArray: EliminationArray<T?> = EliminationArray(
        CAPACITY, TIMEOUT, UNIT
    )

    // 1. Create a new node with given value.
    // 2. Try pushing it to stack.
    // 3a. If successful, return.
    // 3b. Otherwise, try exchanging on elimination array.
    // 4a. If found a matching pop, return.
    // 4b. Otherwise, retry 2.
    fun push(x: T) {
        val n: Node<T> = Node(x) // 1
        while (true) {
            if (tryPush(n)) return  // 2, 3a

            try {
                eliminationArray.visit(x) ?: return // 3b
                // 4a
            } catch (_: TimeoutException) {
            }
        } // 4b
    }

    // 1. Try popping a node from stack.
    // 2a. If successful, return node's value
    // 2b. Otherwise, try exchanging on elimination array.
    // 3a. If found a matching push, return its value.
    // 3b. Otherwise, retry 1.
    @Throws(EmptyStackException::class)
    fun pop(): T {
        while (true) {
            val n: Node<T>? = tryPop() // 1
            if (n != null) return n.item // 2a

            try {
                val y: T? = eliminationArray.visit(null) // 2b
                if (y != null) return y // 3a
            } catch (_: TimeoutException) {
            } // 3b
        }
    }

    // 1. Get stack top.
    // 2. Set node's next to top.
    // 3. Try push node at top (CAS).
    private fun tryPush(n: Node<T>): Boolean {
        val m: Node<T>? = top.get() // 1
        n.next = m // 2
        return top.compareAndSet(m, n) // 3
    }

    // 1. Get stack top, and ensure stack not empty.
    // 2. Try pop node at top, and set top to next (CAS).
    @Throws(EmptyStackException::class)
    private fun tryPop(): Node<T>? {
        val m: Node<T> = top.get() ?: throw EmptyStackException() // 1
        // 1

        val n: Node<T>? = m.next // 2
        return if (top.compareAndSet(m, n)) m else null // 2
    }

    companion object {
        const val CAPACITY: Int = 100
        const val TIMEOUT: Long = 1
        val UNIT: TimeUnit = TimeUnit.MILLISECONDS
    }

    fun top(): T? = top.get()?.item
}