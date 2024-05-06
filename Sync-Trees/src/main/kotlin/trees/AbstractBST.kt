package org.example.trees

import org.example.Node

abstract class AbstractBST<K : Comparable<K>, V> {
    var root: Node<K, V>? = null

    abstract suspend fun insert(key: K, value: V?)

    abstract suspend fun delete(key: K): V?

    abstract suspend fun search(key: K): V?


    protected abstract suspend fun insertNode(key: K, value: V?): Node<K, V>


    protected open suspend fun find(key: K, startingNode: Node<K, V>?): Node<K, V>? {
        return if (startingNode == null) null
        else if (key == startingNode.key) startingNode
        else if (key < startingNode.key) find(key, startingNode.left)
        else find(key, startingNode.right)
    }


    protected open fun deleteNode(node: Node<K, V>) {
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
        } else {
            if (parent.left == nodeToReplace) parent.left = nodeToReplaceWith
            else parent.right = nodeToReplaceWith
        }
        nodeToReplaceWith?.parent = parent
    }

    private fun findSuccessor(node: Node<K, V>): Node<K, V> {
        var successor = node.left ?: throw IllegalStateException("Node must have two children")
        while (successor.right != null) {
            successor = successor.right ?: throw IllegalStateException("Successor must have the right child")
        }
        return successor
    }
}