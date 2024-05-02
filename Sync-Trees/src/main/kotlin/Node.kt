package org.example

import kotlinx.coroutines.sync.Mutex

class Node<K : Comparable<K>, V>(
    var key: K,
    var value: V? = null,
    var left: Node<K, V>? = null,
    var right: Node<K, V>? = null,
    var parent: Node<K, V>? = null
) {
    private val mutex = Mutex()
    suspend fun lock() = mutex.lock()
    fun unlock() = mutex.unlock()
    fun nodeHoldsLock(): Boolean = mutex.isLocked
}
