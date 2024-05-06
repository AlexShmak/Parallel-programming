package org.example.trees

import kotlinx.coroutines.sync.Mutex
import org.example.Node
import java.security.InvalidKeyException

class OptimisticSyncBST<K : Comparable<K>, V> : AbstractBST<K, V>() {

    private  val mutex = Mutex()

    override suspend fun insert(key: K, value: V?) {
        mutex.lock()
        if (root != null) {
            root?.lock()
            mutex.unlock()
            insertNode(key, value)
        } else {
            root = Node(key, value)
            mutex.unlock()
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

//    override suspend fun find(key: K, startingNode: Node<K, V>?): Node<K, V>? {
//        if (startingNode == root) root?.lock()
//        else {
//            val parentNode = startingNode?.parent
//            parentNode?.lock()
//            startingNode?.lock()
//        }
//        var node = startingNode
//        println(node?.nodeHoldsLock())
//        while (true) {
//            if (node == null) return null
//            else if (key == node.key) {
//                println(node.nodeHoldsLock())
//                if (node != root) {
//                    val parentNode = node.parent ?: throw IllegalStateException("Parent node cannot be equal to null")
//                    parentNode.lock()
//                    if (validate(parentNode, node)) {
//                        node.unlock()
//                        parentNode.unlock()
//                        return node
//                    } else {
//                        node.unlock()
//                        parentNode.unlock()
//                        return null
//                    }
//                } else {
//                    node.unlock()
//                    return root
//                }
//            } else if (key < node.key) {
//                node = node.left
//            } else {
//                node = node.right
//            }
//        }
//    }

    override suspend fun find(key: K, startingNode: Node<K, V>?): Node<K, V>? {
        return if (startingNode == null) null
        else if (key == startingNode.key) {
            val nodeToReturn = startingNode
            startingNode.unlock()
            return nodeToReturn
        } else if (key < startingNode.key) {
            val leftSubtree = startingNode.left
            leftSubtree?.lock()
            startingNode.unlock()
            find(key, leftSubtree)
        } else {
            val rightSubtree = startingNode.right
            rightSubtree?.lock()
            startingNode.unlock()
            find(key, rightSubtree)
        }
    }

    private fun validate(parentNode: Node<K, V>?, childNode: Node<K, V>): Boolean {
        if (root == null) return false
        var node = root
        while (true) {
            if (node == parentNode) {
                if (childNode == node?.left || childNode == node?.right) {
                    return true
                }
            } else if (node?.key!! < parentNode!!.key) node = node.right
            else node = node.left
        }
    }
}