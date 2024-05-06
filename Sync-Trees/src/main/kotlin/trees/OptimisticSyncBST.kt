package org.example.trees

import kotlinx.coroutines.sync.Mutex
import org.example.Node
import java.security.InvalidKeyException

class OptimisticSyncBST<K : Comparable<K>, V> : AbstractBST<K, V>() {

    private val treeMutex = Mutex()

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

    /**
     * Inserting a new node into the tree
     */
    override suspend fun insertNode(key: K, value: V?): Node<K, V> {

        var currentNode = root ?: throw IllegalStateException("Current node cannot be equal to null")
        while (true) {
            val comparison = key.compareTo(currentNode.key)
            if (comparison < 0) {
                if (currentNode.left == null) {
                    val newNode = Node(key, value)
                    currentNode.left = newNode
                    newNode.parent = currentNode
                    currentNode.unlock()
                    return newNode
                }
                val nextNode = currentNode.left
                    ?: throw IllegalStateException("Left subtree of the current node cannot be equal to null")
                nextNode.lock()
                currentNode = nextNode
                val parent = currentNode.parent ?: throw IllegalStateException("Parent node cannot be equal to null")
                parent.unlock()
            } else if (comparison > 0) {
                if (currentNode.right == null) {
                    val newNode = Node(key, value)
                    currentNode.right = newNode
                    newNode.parent = currentNode
                    currentNode.unlock()
                    return newNode
                }
                val nextNode = currentNode.right
                    ?: throw IllegalStateException("Right subtree of the current node cannot be equal to null")
                nextNode.lock()
                currentNode = nextNode
                val parent = currentNode.parent ?: throw IllegalStateException("Parent node cannot be equal to null")
                parent.unlock()
            } else throw InvalidKeyException("Such key already exists in the tree")
        }
    }

    /**
     * Deleting a node from the tree
     */
    override suspend fun delete(key: K): V? {
        treeMutex.lock()
        if (root?.key == key) {
            val valueToReturn = root?.value
            deleteNode(root!!)
            treeMutex.unlock()
            return valueToReturn
        }
        treeMutex.unlock()
        val node = find(key, root) ?: return null
        if (node == root) {
            val returnValue = node.value
            deleteNode(node)
            println("here 1")
            return returnValue
        }
        deleteNode(node)
        println("here 2")
        return node.value
    }

    /**
     * Searching a specific node in the tree
     */
    override suspend fun search(key: K): V? {
        val node = find(key, root)
        return node?.value
    }

    override suspend fun find(key: K, startingNode: Node<K, V>?): Node<K, V>? {
        var currentNode = startingNode
        while (true) {
            if (currentNode == null) {
                return null
            }
            if (key == currentNode.key) {
                currentNode.lock()
                if (validate(currentNode) && currentNode.key == key) {
                    try {
                        currentNode.unlock()
                    } catch (e: IllegalStateException) {
                        throw IllegalStateException("Failed to unlock node in find function (curNode.key == key)")
                    }
                    return currentNode
                } else {
                    currentNode.unlock()
                    return null
                }
            } else if (key < currentNode.key) {
                val left = currentNode.left
                currentNode = left
            } else {
                val right = currentNode.right
                currentNode = right
            }
        }
    }

    /**
     * Validation function to check that the node is still present in the tree
     */
    private fun validate(childNode: Node<K, V>): Boolean {
        if (root == null) return false
        var node = root
        while (true) {
            node = if (node == childNode) {
                return true
            } else if (node?.key!! < childNode.key) node.right
            else node.left
        }
    }
}