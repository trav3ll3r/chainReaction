package au.com.beba.chainReaction

import junit.framework.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ParallelDemoTest {
    private val parallelExecutor: ExecutorService = Executors.newFixedThreadPool(4)
    private val taskName = "Parallel Task"

    private var results = mutableListOf<Int>()

    @Test
    fun goWithClosureWrappedIntoCallableClosure() {
        results = mutableListOf()

        parallelExecutor.invokeAll(
                mutableListOf(
                        Callable { getTaskAsEmptyClosure(1, 500) },
                        Callable { getTaskAsEmptyClosure(2, 300) },
                        Callable { getTaskAsEmptyClosure(3) }
                ))
        assertResults()
    }

    private fun getTaskAsEmptyClosure(id: Int, duration: Long = 200): () -> Any? {
        System.out.printf("%s #%d  - START\n".format(taskName, id))
        Thread.sleep(duration)
        registerResult(id)
        System.out.printf("%s #%d - END\n".format(taskName, id))
        return {}
    }

    @Test
    fun goWithClosureWrappedIntoCallable() {
        results = mutableListOf()
        parallelExecutor.invokeAll(
                mutableListOf(
                        Callable(getTaskAsClosure(1, 500)),
                        Callable(getTaskAsClosure(2, 300)),
                        Callable(getTaskAsClosure(3))
                ))
        assertResults()
    }

    private fun getTaskAsClosure(id: Int, duration: Long = 200): () -> Any? {
        return {
            System.out.printf("%s #%d  - START\n".format(taskName, id))
            Thread.sleep(duration)
            registerResult(id)
            System.out.printf("%s #%d - END\n".format(taskName, id))
        }
    }

    @Test
    fun goWithCallable() {
        results = mutableListOf()
        parallelExecutor.invokeAll(
                mutableListOf(
                        getTaskCallable(1, 500),
                        getTaskCallable(2, 300),
                        getTaskCallable(3)
                ))
        assertResults()
    }

    private fun getTaskCallable(id: Int, duration: Long = 200): Callable<Any> {
        return Callable {
            System.out.printf("%s #%d - START\n".format(taskName, id))
            Thread.sleep(duration)
            registerResult(id)
            System.out.printf("%s #%d - END\n".format(taskName, id))
        }
    }

    private fun registerResult(id: Int) {
        results.add(id)
    }

    private fun assertResults() {
        System.out.printf("assertResults\n")
        val exp = listOf(3, 2, 1)
        assertEquals("Results does not match expected value", exp, results)
    }
}