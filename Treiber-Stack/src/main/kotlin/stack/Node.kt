package stack
/**
 * A generic singly linked list node.
 *
 * @param T the type of the node's data
 * @property item the data stored in the node
 * @property next the next node in the list, or null if this is the last node
 */
class Node<T>(
    val item: T,
    var next: Node<T>? = null
)