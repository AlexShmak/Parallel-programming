package org.example.trees

import org.example.Node

class OptimisticSyncBST<K:Comparable<K>, V>:AbstractBST<K,V>() {
    override suspend fun insert(key: K, value: V?) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(key: K): V? {
        TODO("Not yet implemented")
    }

    override suspend fun search(key: K): V? {
        TODO("Not yet implemented")
    }

    override suspend fun insertNode(key: K, value: V?): Node<K, V> {
        TODO("Not yet implemented")
    }
}