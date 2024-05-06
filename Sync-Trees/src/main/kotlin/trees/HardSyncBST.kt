package org.example.trees

import kotlinx.coroutines.sync.Mutex
import org.example.Node
import java.security.InvalidKeyException

class HardSyncBST<K : Comparable<K>, V> : AbstractBST<K, V>() {

    private val mutex = Mutex()

    /**
     * Inserting a new node into the tree
     */
    override suspend fun insert(key: K, value: V?) {
        mutex.lock()
        insertNode(key, value)
        mutex.unlock()
    }

    override suspend fun insertNode(key: K, value: V?): Node<K, V> {
        if (root == null) {
            val newNode = Node(key, value)
            root = newNode
            return newNode
        }

        var currentNode = root ?: throw IllegalStateException("Current node cannot be equal to null")
        while (true) {
            val comparison = key.compareTo(currentNode.key)
            if (comparison < 0) {
                if (currentNode.left == null) {
                    val newNode = Node(key, value)
                    currentNode.left = newNode
                    newNode.parent = currentNode
                    return newNode
                }
                currentNode = currentNode.left
                    ?: throw IllegalStateException("Left subtree of the current node cannot be equal to null")
            } else if (comparison > 0) {
                if (currentNode.right == null) {
                    val newNode = Node(key, value)
                    currentNode.right = newNode
                    newNode.parent = currentNode
                    return newNode
                }
                currentNode = currentNode.right
                    ?: throw IllegalStateException("Right subtree of the current node cannot be equal to null")
            } else throw InvalidKeyException("Such key already exists in the tree")
        }
    }

    /**
     * Deleting a node from the tree
     */
    override suspend fun delete(key: K): V? {
        mutex.lock()
        val node = find(key, root) ?: return null
        val returnValue = node.value
        deleteNode(node)
        mutex.unlock()
        return returnValue
    }


    /**
     * Searching a specific node in the tree
     */
    override suspend fun search(key: K): V? {
        mutex.lock()
        val node = find(key, root)
        mutex.unlock()
        return node?.value
    }

}