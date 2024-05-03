package org.example.trees

import kotlinx.coroutines.sync.Mutex
import org.example.Node
import java.security.InvalidKeyException

class SoftSyncBST<K : Comparable<K>, V> : AbstractBST<K, V>() {

    private val treeMutex = Mutex()
    private val mutex = Mutex()


    /**
     * Inserting a new node into the tree
     */
    override suspend fun insert(key: K, value: V?) {
        treeMutex.lock()
        if (root != null) {
            root?.lock()
            treeMutex.unlock()
            insertNode(key, value)
        } else {
            root = Node(key, value)
            treeMutex.unlock()
        }
    }

    override suspend fun insertNode(key: K, value: V?): Node<K, V> {

        var currentNode = root ?: throw IllegalStateException("Current node cannot be equal to null")
        while (true) {
            val comparison = key.compareTo(currentNode.key)
            if (comparison < 0) {
                if (currentNode.left == null) {
                    val newNode = Node(key, value)
                    currentNode.left = newNode
                    newNode.parent = currentNode
                    println(currentNode.nodeHoldsLock())
                    currentNode.unlock()
                    return newNode
                }
                val nextNode = currentNode.left
                    ?: throw IllegalStateException("Left subtree of the current node cannot be equal to null")
                nextNode.lock()
                currentNode = nextNode
                currentNode.unlock()
            } else if (comparison > 0) {
                if (currentNode.right == null) {
                    val newNode = Node(key, value)
                    currentNode.right = newNode
                    newNode.lock()
                    newNode.parent = currentNode
                    newNode.unlock()
                    currentNode.unlock()
                    return newNode
                }
                val nextNode = currentNode.left
                    ?: throw IllegalStateException("Left subtree of the current node cannot be equal to null")
                nextNode.lock()
                currentNode = nextNode
                currentNode.unlock()
            } else throw InvalidKeyException("Such key already exists in the tree")
        }
    }

    /**
     * Deleting a node from the tree
     */
    override suspend fun delete(key: K): V? {
        val node = find(key, root) ?: return null
        val parent = node.parent
        if (parent != null) {
            parent.lock()
        } else treeMutex.lock()
        node.lock()
        deleteNode(node)
        node.unlock()
        if (parent != null) {
            parent.unlock()
        } else treeMutex.unlock()
        val returnValue = node.value
        return returnValue
    }

    /**
     * Searching a specific node in the tree
     */
    override suspend fun search(key: K): V? {
        val node = find(key, root)
        return node?.value
    }

}
