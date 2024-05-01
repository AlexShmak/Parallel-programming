package org.example.trees

import org.example.Node

abstract class AbstractBST<K : Comparable<K>, V> {
    var root: Node<K, V>? = null

    abstract suspend fun search(key: K): V?
    protected abstract fun find(key: K, root: Node<K, V>?): Node<K, V>?
    abstract suspend fun insert(key: K, value: V?)
    abstract suspend fun delete(key: K): V?
}