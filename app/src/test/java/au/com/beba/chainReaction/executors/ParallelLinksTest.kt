package au.com.beba.chainReaction.executors

import au.com.beba.chainreaction.chain.ChainCallback
import junit.framework.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorCompletionService
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

//private val parallelExecutor: ExecutorService = Executors.newFixedThreadPool(3)
private val parallelExecutor: ExecutorService = Executors.newCachedThreadPool()


private var results = mutableListOf<TaskId>()

fun storeResult(logger: Logger, level: Int, newElement: TaskId) {
    logger.log(level, "Storing result %s".format(newElement))
    results.add(newElement)
}

class ParallelLinksTest {

    @Test
    fun goWithClosureWrappedIntoCallable() {

        val logger = Logger()

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
            storeResult(logger, 1, it.get())
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
    private lateinit var parentCallback: ChainCallback
    private val links: MutableList<TaskAsCallable> = mutableListOf()
    private var linksExecutionStrategy: ExecutionStrategy = ExecutionStrategy.SERIAL

    // CHAIN STATUS
    private var chainStatus: ChainCallback.Status = ChainCallback.Status.NOT_STARTED

    val status: ChainCallback.Status
        get() { return chainStatus}

    private val childChainCallback = object : ChainCallback {
        override fun onDone(status: ChainCallback.Status) {
            logger.log(level, "childChainCallback.onDone")
            reactionsPhase(true)
//            logger.log(level, "childChainCallback | onDone | finish")
        }
    }

    init {
        level = 1
    }

    fun add(vararg subChains: TaskAsCallable): TaskAsCallable {
        subChains.forEach {
            it.setLevel(level + 1)
            it.setParentCallback(childChainCallback)
            links.add(it)
        }

        return this
    }

    private fun setLevel(newLevel: Int) {
        this.level = newLevel
        links.forEach { it.setLevel(this.level + 1) }
    }

    private fun setParentCallback(parentCallback: ChainCallback) {
        this.parentCallback = parentCallback
    }

    fun setConcurrencyStrategy(value: ExecutionStrategy): TaskAsCallable {
        linksExecutionStrategy = value
        return this
    }

    override fun call(): TaskId {
        logger.log(level, "%s #%s START @ %s".format(this::class.java.simpleName, id, Thread.currentThread().name))
//        preMainPhase()
        mainPhase()
//        postMainPhase()
        linksPhase()
        reactionsPhase()
        logger.log(level, "%s #%s END".format(this::class.java.simpleName, id))
        return id
    }

    private fun preMainPhase() {
        logger.log(level, "preMainPhase")
        Thread.sleep(10)
    }

    private fun mainPhase() {
        logger.log(level, "mainPhase")
        Thread.sleep(duration)
    }

    private fun postMainPhase() {
        logger.log(level, "postMainPhase")
        Thread.sleep(10)
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
                storeResult(logger, level, res)
            }
        } else {
            logger.log(level, "%s has no sub-chains".format(id))
        }
//        finishPhase()
    }

    private fun reactionsPhase(skipDecision: Boolean = false) {
        logger.log(level, "reactionsPhase")
        runReactions()
        if (!skipDecision) {
            return decisionPhase()
        }
    }

    private fun decisionPhase() {
        logger.log(level, "decisionPhase")
        //Thread.sleep(50)

        val nextLinks = links.filter { it.chainStatus !in listOf(ChainCallback.Status.SUCCESS, ChainCallback.Status.ERROR) }
        if (nextLinks.isNotEmpty()) {
            onDecisionDone(ChainCallback.Status.SUCCESS)
        } else {
            // ALL LINKS IN_PROGRESS BUT SOME STILL MISSING RESULT (WAITING FOR ALL Chain Links TO OBTAIN RESULT)
           logger.log(level, "waiting for all results")
        }

    }

    private fun onDecisionDone(finalStatus: ChainCallback.Status) {
        chainStatus = finalStatus
        finishPhase()
    }

//    private fun onDecisionNotDone() {
//        //POSSIBLY NOT NEEDED
//    }

    private fun finishPhase() {
        logger.log(level, "finishPhase")
        Thread.sleep(50)
        childChainCallback.onDone(ChainCallback.Status.SUCCESS)
    }

    /* ************** */
    /* FOR OVERRIDING */
    /* ************** */
    @Suppress("MemberVisibilityCanBePrivate")
    fun runReactions() {
        Thread.sleep(50)
    }
}