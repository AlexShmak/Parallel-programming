package stack

import java.util.concurrent.atomic.AtomicReference


/**
 * Concurrent stack implementation using Treiber's algorithm
 */
class TreiberStack<T> : AbstractStack<T>() {
    private val head = AtomicReference<Node<T>?>(null)

    /**
     * Pushes an element onto the stack
     */
    override fun push(item: T) {
        do {
            val oldHead = head.get()
            val newHead = Node(item, oldHead)
        } while (!head.compareAndSet(oldHead, newHead))
    }

    /**
     * Removes and returns the element at the top of the stack
     */
    override fun pop(): T? {
        var oldHead: Node<T>
        do {
            oldHead = head.get()?: return null
            val newHead = oldHead.next
        } while (!head.compareAndSet(oldHead, newHead))
        return oldHead.item
    }

    /**
     * Returns the element at the top of the stack without removing it
     */
    fun top(): T? = head.get()?.item
}