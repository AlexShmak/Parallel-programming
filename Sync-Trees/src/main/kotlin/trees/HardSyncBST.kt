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

    private fun insertNode(key: K, value: V?): Node<K, V> {
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
        deleteNode(node)
        val returnValue = node.value
        mutex.unlock()
        return returnValue
    }

    private fun deleteNode(node: Node<K, V>) {
        if (node.left == null && node.right == null) deleteLeaf(node)
        else if (node.left == null || node.right == null) deleteNodeWithOneChild(node)
        else deleteNodeWithTwoChildren(node)
    }

    private fun deleteNodeWithOneChild(node: Node<K, V>) {
        val newNode = if (node.left == null) node.right else node.left
        replaceNode(node, newNode)
    }

    private fun deleteNodeWithTwoChildren(node: Node<K, V>) {
        val successor = findSuccessor(node)
        node.key = successor.key
        node.value = successor.value
        deleteNode(successor)
    }

    private fun deleteLeaf(node: Node<K, V>) {
        replaceNode(node, null)
    }

    private fun replaceNode(nodeToReplace: Node<K, V>, nodeToReplaceWith: Node<K, V>?) {
        val parent = nodeToReplace.parent
        if (parent == null) {
            root = nodeToReplaceWith
        } else if (parent.left == nodeToReplace) parent.left = nodeToReplaceWith
        else parent.right = nodeToReplaceWith
        nodeToReplaceWith?.parent = parent
    }

    private fun findSuccessor(node: Node<K, V>): Node<K, V> {
        var successor = node.left ?: throw IllegalStateException("Node must have two children")
        while (successor.right != null) {
            successor = successor.right ?: throw IllegalStateException("Successor must have the right child")
        }
        return successor
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

    override fun find(key: K, root: Node<K, V>?): Node<K, V>? {
        return if (root == null) null
        else if (key == root.key) root
        else if (key < root.key) find(key, root.left)
        else find(key, root.right)
    }
}