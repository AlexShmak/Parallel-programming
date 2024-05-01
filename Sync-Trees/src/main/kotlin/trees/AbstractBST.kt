package org.example.trees

import org.example.Node

abstract class AbstractBST<K : Comparable<K>, V> {
    protected var root: Node<K, V>? = null

    protected abstract fun search(key: K): V?
    protected abstract fun find(key: K, root: Node<K, V>?): Node<K, V>?
    protected abstract fun insert(key: K, value: V?)
    protected abstract fun delete(key: K):K?
}