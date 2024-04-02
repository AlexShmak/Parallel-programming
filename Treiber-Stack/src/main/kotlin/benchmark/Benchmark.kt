package srack.benchmark

import stack.TreiberStack
import kotlin.random.Random

class Benchmark(
    private val stack: TreiberStack<Int>,
    private val workload: Long,
) {

    init {
        require(stack.empty())
        require(workload > 0)
    }

    fun perform(threadCount: Int, timeLimit: Int): Int {
        var operationCount = 0
        val startTime = System.currentTimeMillis()
        val threads = Array(threadCount) {
            Thread {
                while (System.currentTimeMillis() - startTime < timeLimit.toLong()) {
                    stack.push(1)
                    stack.pop()
                    operationCount += 2
                    Thread.sleep(Random.nextLong(0, workload))
                }
            }
        }

        threads.forEach { it.start() }
        threads.forEach { it.join() }

        return operationCount
    }


}