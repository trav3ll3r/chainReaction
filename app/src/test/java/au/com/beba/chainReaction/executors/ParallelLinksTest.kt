package au.com.beba.chainReaction.executors

import junit.framework.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.*

//private val parallelExecutor: ExecutorService = Executors.newFixedThreadPool(3)
private val parallelExecutor: ExecutorService = Executors.newCachedThreadPool()
private var results = mutableListOf<TaskId>()

class ParallelLinksTest {

    @Test
    fun goWithClosureWrappedIntoCallable() {

        val a1 = TaskAsCallable("a1", 200)
        val b2 = TaskAsCallable("b2", 200)
        val b21 = TaskAsCallable("b21", 1000)
        val b211 = TaskAsCallable("b211", 1000)
        val b22 = TaskAsCallable("b22", 500)
        val c2 = TaskAsCallable("c2", 300)

        a1.add(
                b2
                        .add(
                                b21
                                        .add(b211),
                                b22)
                        .setConcurrencyStrategy(ExecutionStrategy.PARALLEL),
                c2
        )

        val tasksLevel1Futures: List<Future<TaskId>> = parallelExecutor.invokeAll(mutableListOf(a1))

        tasksLevel1Futures.forEach {
            results.add(it.get())
        }
        assertResults()
    }

    private fun assertResults() {
        System.out.println("assertResults")
        val exp = listOf("b22", "b211", "b21", "b2", "c2", "a1")
        assertEquals("Results does not match expected value", exp, results)
    }
}

/**
 * TODO: Builder for [ExecutionStrategy]
 */
class TaskAsCallable(private val id: TaskId, private val duration: Long = 200) : Callable<TaskId> {
    private val logger: Logger = Logger()
    private var level: Int = 0
    private val links: MutableList<TaskAsCallable> = mutableListOf()
    var linksExecutionStrategy: ExecutionStrategy = ExecutionStrategy.SERIAL

    init {
        level = 1
    }

    fun add(vararg subChains: TaskAsCallable): TaskAsCallable {
        subChains.forEach {
            it.level = level + 1
            links.add(it)
        }

        return this
    }

    fun getConcurrencyStrategy(): ExecutionStrategy {
        return linksExecutionStrategy
    }

    fun setConcurrencyStrategy(value: ExecutionStrategy): TaskAsCallable {
        linksExecutionStrategy = value
        return this
    }


    override fun call(): TaskId {
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
            val completionService: ExecutorCompletionService<TaskId> = ExecutorCompletionService(internalExecutor)

            links.forEach {
                completionService.submit(it)
            }

            links.forEach {
                val future = completionService.take()
                val res = future.get()
                results.add(res)
            }
        } else {
            logger.log(level, "%s has no sub-chains".format(id))
        }
    }
}