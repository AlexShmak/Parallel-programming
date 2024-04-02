package srack

import srack.benchmark.Benchmark
import stack.TreiberStack

import stack.eliminationBackoffStack.EliminationBackoffStack

fun main() {

    val stackT = TreiberStack<Int>()
    val stackE = EliminationBackoffStack<Int>()

    val timeLimit = intArrayOf(1000, 2000, 4000, 8000)
    val threads = intArrayOf(1, 2, 4, 8, 14, 32)
    val workload = 100L

    val benchmarkT = Benchmark(stackT, workload)
    val benchmarkE = Benchmark(stackE, workload)
    for (j in threads) {
        println("Threads: $j")
        println("TreiberStack")

        for (i in timeLimit) {
            var resultT = 0
            for (k in 1..5) {
                resultT += benchmarkT.perform(j, i)
            }
            print("${resultT / 5} ")
        }
        println("\n")

        println("EliminationBackoffStack")
        for (i in timeLimit) {
            var resultE = 0
            for (k in 1..5) {
                resultE += benchmarkE.perform(j, i)
            }
            print("${resultE / 5} ")
        }

        println("\n")
    }


}