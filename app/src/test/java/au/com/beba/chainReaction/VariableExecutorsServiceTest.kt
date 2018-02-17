package au.com.beba.chainReaction

import junit.framework.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class VariableExecutorsServiceTest {
    private val parallelExecutor: ExecutorService = Executors.newFixedThreadPool(4)
    private val serialExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    private val taskName = "Task"

    private var results = mutableListOf<Int>()

    @Test
    fun emptyClosureOnSerial() {
        testEmptyClosure(serialExecutor)
        assertSerialResults()
    }

    @Test
    fun emptyClosureOnParallel() {
        testEmptyClosure(parallelExecutor)
        assertParallelResults()
    }

    private fun getTaskAsEmptyClosure(id: Int, duration: Long = 200): () -> Any? {
        System.out.printf("%s #%d  - START\n".format(taskName, id))
        Thread.sleep(duration)
        registerResult(id)
        System.out.printf("%s #%d - END\n".format(taskName, id))
        return {}
    }

    @Test
    fun closureWithBodyOnSerial() {
        testClosureWithBody(serialExecutor)
        assertSerialResults()
    }

    @Test
    fun closureWithBodyOnParallel() {
        testClosureWithBody(parallelExecutor)
        assertParallelResults()
    }

    private fun getTaskAsClosure(id: Int, duration: Long = 200): () -> Any? {
        return {
            System.out.printf("%s #%d  - START\n".format(taskName, id))
            Thread.sleep(duration)
            registerResult(id)
            System.out.printf("%s #%d - END\n".format(taskName, id))
        }
    }

    private fun testEmptyClosure(executorService: ExecutorService) {
        results = mutableListOf()

        executorService.invokeAll(
                mutableListOf(
                        Callable { getTaskAsEmptyClosure(1, 500) },
                        Callable { getTaskAsEmptyClosure(2, 300) },
                        Callable { getTaskAsEmptyClosure(3) }
                ))
    }

    private fun testClosureWithBody(executorService: ExecutorService) {
        results = mutableListOf()

        executorService.invokeAll(
                mutableListOf(
                        Callable(getTaskAsClosure(1, 500)),
                        Callable(getTaskAsClosure(2, 300)),
                        Callable(getTaskAsClosure(3))
                ))
    }

    private fun registerResult(id: Int) {
        results.add(id)
    }

    private fun assertSerialResults() {
        System.out.printf("assertSerialResults\n")
        val exp = listOf(1, 2, 3)
        assertEquals("Results does not match expected value", exp, results)
    }

    private fun assertParallelResults() {
        System.out.printf("assertParallelResults\n")
        val exp = listOf(3, 2, 1)
        assertEquals("Results does not match expected value", exp, results)
    }
}