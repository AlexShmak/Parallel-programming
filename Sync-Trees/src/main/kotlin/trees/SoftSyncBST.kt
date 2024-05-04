package org.example.trees

import kotlinx.coroutines.sync.Mutex
import org.example.Node
import java.security.InvalidKeyException

class SoftSyncBST<K : Comparable<K>, V> : AbstractBST<K, V>() {

    private val treeMutex = Mutex()

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
        root?.lock()
        val node = find(key, root) ?: return null
        node.lock()
        val parent = node.parent
        parent?.lock()
        deleteNode(node)
        node.unlock()
        parent?.unlock()
        val returnValue = node.value
        return returnValue
    }

    /**
     * Searching a specific node in the tree
     */
    override suspend fun search(key: K): V? {
        root?.lock()
        val node = find(key, root)
        return node?.value
    }

    override suspend fun find(key: K, root: Node<K, V>?): Node<K, V>? {
        return if (root == null) null
        else if (key == root.key) {
            val nodeToReturn = root
            root.unlock()
            nodeToReturn
        } else if (key < root.key) {
            val leftSubtree = root.left
            leftSubtree?.lock()
            root.unlock()
            find(key, leftSubtree)
        } else {
            val rightSubtree = root.right
            rightSubtree?.lock()
            root.unlock()
            find(key, rightSubtree)
        }
    }

}
