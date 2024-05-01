package org.example

data class Node<K : Comparable<K>, V>(
    var key: K,
    var value: V? = null,
    var left: Node<K, V>? = null,
    var right: Node<K, V>? = null,
    var parent: Node<K, V>? = null
)
