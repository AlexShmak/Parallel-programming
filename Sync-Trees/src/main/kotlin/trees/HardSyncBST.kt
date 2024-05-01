package org.example.trees

import kotlinx.coroutines.sync.Mutex
import org.example.Node
import java.security.InvalidKeyException

class HardSyncBST<K : Comparable<K>, V> : AbstractBST<K, V>() {
    val mutex = Mutex()

    /**
     * Inserting a new node into the tree
     */
    override fun insert(key: K, value: V?) {
        insertNode(key, value)
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
    override fun delete(key: K): K? {
        val node = find(key, root) ?: return null
        deleteNode(key)
        return node.key
    }

    private fun deleteNode(key: K) {
        TODO("not yet implemented")
    }

    private fun deleteNodeWithOneChild(node: Node<K, V>) {
        val nodeParent = findParent(node)

    }

    private fun deleteNodeWithTwoChildren(node: Node<K, V>) {
        TODO("not yet")
    }

    private fun deleteLeaf(node: Node<K, V>) {
        replaceNode(node, null)
    }

    private fun replaceNode(nodeToReplace: Node<K, V>?, nodeToReplaceWith: Node<K, V>?) {
        TODO("not yet")
    }

    private fun findParent(node: Node<K, V>): Node<K, V>? {
        return if (node == root || root == null) null
        else if (root?.left == node || root?.right == node) root
        else if (node.key < root?.key!!) findParent(root?.left!!)
        else findParent(root?.right!!)
    }

    /**
     * Searching a specific node in the tree
     */
    override fun search(key: K): V? {
        val node = find(key, root) ?: throw InvalidKeyException("No such key in the tree")
        return node.value
    }

    override fun find(key: K, root: Node<K, V>?): Node<K, V>? {
        return if (root == null) null
        else if (key == root.key) root
        else if (key < root.key) find(key, root.left)
        else find(key, root.right)
    }
}