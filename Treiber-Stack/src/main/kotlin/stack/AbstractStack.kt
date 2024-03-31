package stack

abstract class AbstractStack<T> {
    abstract fun push(item: T)
    abstract fun pop(): T?
}