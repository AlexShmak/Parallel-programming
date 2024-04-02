package stack

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.jetbrains.kotlinx.lincheck.annotations.*
import org.jetbrains.kotlinx.lincheck.*
import org.jetbrains.kotlinx.lincheck.strategy.managed.modelchecking.ModelCheckingOptions
import org.jetbrains.kotlinx.lincheck.strategy.stress.*

class TreiberStackTest {
    private val stack = TreiberStack<Int>()

    @Operation
    fun push(item: Int) = stack.push(item)

    @Operation
    fun pop() = stack.pop()

    @Test
    fun stressTest() = StressOptions()
        .actorsBefore(2) // Number of operations before the parallel part
        .threads(2) // Number of threads in the parallel part
        .actorsPerThread(2) // Number of operations in each thread of the parallel part
        .actorsAfter(1) // Number of operations after the parallel part
        .iterations(100) // Generate 100 random concurrent scenarios
        .invocationsPerIteration(1000) // Run each generated scenario 100 times
        .check(this::class) // Run the test

    @Test
    fun modelCheckingTest() = ModelCheckingOptions() // Stress testing options:
        .actorsBefore(2) // Number of operations before the parallel part
        .threads(2) // Number of threads in the parallel part
        .actorsPerThread(2) // Number of operations in each thread of the parallel part
        .actorsAfter(1) // Number of operations after the parallel part
        .iterations(100) // Generate 100 random concurrent scenarios
        .invocationsPerIteration(1000) // Run each generated scenario 100 times
        .check(this::class) // Run the test

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