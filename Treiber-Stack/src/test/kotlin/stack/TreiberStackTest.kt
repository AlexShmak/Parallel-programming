package stack

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.jetbrains.kotlinx.lincheck.annotations.*
import org.jetbrains.kotlinx.lincheck.*
import org.jetbrains.kotlinx.lincheck.strategy.stress.*

class TreiberStackTest {
    private val stack = TreiberStack<Int>()

    @Operation
    fun push(item: Int) = stack.push(item)

    @Operation
    fun pop() = stack.pop()

    @Test
    fun stressTest() = StressOptions().check(this::class)

    @Test
    fun `test push fun`() {
        stack.push(1)
        assertEquals(1, stack.top())
    }

    @Test
    fun `test pop fun`() {
        stack.push(1)
        stack.pop()
        assertEquals(null, stack.top())
    }

}