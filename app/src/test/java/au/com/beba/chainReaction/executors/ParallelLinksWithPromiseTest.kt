package au.com.beba.chainReaction.executors

import junit.framework.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.*
import java.util.function.Supplier

//private val parallelExecutor: ExecutorService = Executors.newFixedThreadPool(3)
private val parallelExecutor: ExecutorService = Executors.newCachedThreadPool()
private var results = mutableListOf<TaskId>()
private val logger: Logger = Logger()

class ParallelLinksWithPromiseTest {

    @Test
    fun goWithClosureWrappedIntoCallable() {

        val a1 = TaskWithPromise(1, "a1", 200)
        val b2 = TaskWithPromise(2, "b2", 200)
        val b21 = TaskWithPromise(3, "b21", 2000)
        val b22 = TaskWithPromise(3, "b22", 500)
        val c2 = TaskWithPromise(2, "c2", 300)

        a1.add(b2)
        b2.add(b21, b22)
        b2.linksExecutionStrategy = ExecutionStrategy.PARALLEL
        a1.add(c2)

        val promise = CompletableFuture.supplyAsync(a1, parallelExecutor)
        promise.get()
        promise.thenAccept { results.add(it) }

        assertResults()
    }

    private fun assertResults() {
        System.out.println("assertResults")
        val exp = listOf("b22", "b21", "b2", "c2", "a1")
        assertEquals("Results does not match expected value", exp, results)
    }
}

//var momsPurse: Supplier<TaskId> = Supplier {
//    try {
//        Thread.sleep(1000)//mom is busy
//    } catch (e: InterruptedException) {
//    }
//    "100"
//}


/**
 * TODO: Builder for [add] and [ExecutionStrategy]
 */
class TaskWithPromise(private val level: Int, private val id: TaskId, private val duration: Long = 200) : Supplier<TaskId> {
    private val logger: Logger = Logger()
    private val links: MutableList<TaskWithPromise> = mutableListOf()
    var linksExecutionStrategy: ExecutionStrategy = ExecutionStrategy.SERIAL

    fun add(vararg subChains: TaskWithPromise) {
        links.addAll(subChains)
    }

    override fun get(): TaskId {
        logger.log(level, "%s #%s START @ %s".format(this::class.java.simpleName, id, Thread.currentThread().name))
        Thread.sleep(duration)
        linksPhase()
        logger.log(level, "%s #%s END".format(this::class.java.simpleName, id))
        return id
    }

    private fun linksPhase() {
        logger.log(level, "linksPhase")
        if (links.isNotEmpty()) {
            logger.log(level, "%s is starting sub-chains".format(id))
            val internalExecutor: ExecutorService = when (linksExecutionStrategy) {
                ExecutionStrategy.SERIAL -> Executors.newSingleThreadExecutor()
                ExecutionStrategy.PARALLEL -> Executors.newCachedThreadPool()
            }

            links.forEach {
                val promise = CompletableFuture.supplyAsync(it, internalExecutor).thenAccept { results.add(it) }
                while (!promise.isDone) {
                    promise.get(3000, TimeUnit.MILLISECONDS)
                }
            }
        } else {
            logger.log(level, "%s has no sub-chains".format(id))
        }
    }
}
