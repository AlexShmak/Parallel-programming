package trees

import org.junit.jupiter.api.Assertions.*
import kotlinx.coroutines.*
import org.example.trees.AbstractBST
import org.junit.jupiter.api.Test
import kotlin.random.Random

abstract class GeneralTests<T : AbstractBST<Int, String>>(
    treeType: () -> T,
    private val nodes: Int = 100
) {
    private fun timeDelay() = Random.nextLong(100)

    private val tree: T = treeType()
    private var randomNodes = (0..99).shuffled().take(nodes)

    @Test
    fun `test insert method`() {
        // Fill tree with elements
        runBlocking {
            coroutineScope {
                repeat(nodes) {
                    launch(Dispatchers.Default) {
                        delay(timeDelay())
                        tree.insert(randomNodes[it], randomNodes[it].toString())
                    }
                }
            }
        }

        runBlocking {
            for (i in randomNodes) {
                assertEquals(i.toString(), tree.search(i))
            }
        }

    }

    @Test
    fun `test delete method`() {
        runBlocking {
            repeat(nodes) {
                tree.insert(randomNodes[it], randomNodes[it].toString())
            }
        }

        runBlocking {
            randomNodes = randomNodes.shuffled(Random)
            repeat(nodes) {
                launch(Dispatchers.Default) {
                    delay(timeDelay())
                    tree.delete(randomNodes[it])
                }
            }
        }

        runBlocking {
            for (key in randomNodes) {
                assertEquals(null, tree.search(key))
            }
        }


    }

    @Test
    fun `test parallel execution`() {
        runBlocking {
            coroutineScope {
                repeat(nodes) {
                    launch(Dispatchers.Default) {
                        delay(timeDelay())
                        tree.insert(randomNodes[it], randomNodes[it].toString())
                    }
                }
            }
        }

        val nodesToDelete = randomNodes.shuffled(Random).take(nodes / 2)

        runBlocking {
            coroutineScope {
                repeat(nodes / 2) {
                    launch(Dispatchers.Default) {
                        delay(timeDelay())
                        tree.delete(nodesToDelete[it])
                    }
                }
            }
        }

        runBlocking {
            for (key in randomNodes) {
                if (key !in nodesToDelete) {
                    assertEquals(key.toString(), tree.search(key))
                } else assertEquals(null, tree.search(key))
            }
        }
    }

}