package au.com.beba.chainReaction.executors

import au.com.beba.chainreaction.chain.ChainCallback
import junit.framework.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorCompletionService
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

//private val parallelExecutor: ExecutorService = Executors.newFixedThreadPool(3)
private val parallelExecutor: ExecutorService = Executors.newCachedThreadPool()


private var results = mutableListOf<TaskId>()

fun storeResult(logger: Logger, level: Int, newElement: TaskId) {
    logger.log(level, newElement, "Storing result %s".format(newElement))
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

        //[b22, b211, b21, b2, c2, a1] EXPECTED
        a1.add(
                b2
                        .setConcurrencyStrategy(ExecutionStrategy.PARALLEL)
                        .add(
                                b21
                                        .add(b211),
                                b22)
                ,
                c2
        )

        a1.setParentCallback(object : ChainCallback<TaskAsCallable> {
            override fun onDone(completedChain: TaskAsCallable) {
                if (completedChain.getResult() is String) {
                    storeResult(logger, completedChain.getLevel(), completedChain.getResult() as String)
                }
                assertResults()
            }
        })

        parallelExecutor.submit(a1).get()
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
    private lateinit var parentCallback: ChainCallback<TaskAsCallable>
    private val links: MutableList<TaskAsCallable> = mutableListOf()
    private var linksExecutionStrategy: ExecutionStrategy = ExecutionStrategy.SERIAL
    private var result: Any? = null

    private var chainStatus: ChainCallback.Status = ChainCallback.Status.NOT_STARTED

    fun getLevel(): Int {
        return level
    }

    fun getResult(): Any? {
        return result
    }

    private val childChainCallback: ChainCallback<TaskAsCallable> = object : ChainCallback<TaskAsCallable> {
        override fun onDone(completedChain: TaskAsCallable) {
            logger.log(level, id, "childChainCallback.onDone")
            storeResult(logger, level, (completedChain.getResult() as String))
            reactionsPhase()
        }
    }

    init {
        level = 1
    }

    private fun setLevel(newLevel: Int): TaskAsCallable {
        this.level = newLevel
        links.forEach { it.setLevel(this.level + 1) }
        return this
    }

    fun add(vararg subChains: TaskAsCallable): TaskAsCallable {
        subChains.forEach {
            it.setLevel(level + 1)
            it.setParentCallback(childChainCallback)
            links.add(it)
        }

        return this
    }

    fun setParentCallback(parentCallback: ChainCallback<TaskAsCallable>): TaskAsCallable {
        this.parentCallback = parentCallback
        return this
    }

    fun setConcurrencyStrategy(value: ExecutionStrategy): TaskAsCallable {
        linksExecutionStrategy = value
        return this
    }

    override fun call(): TaskId {
        logger.log(level, "%s #%s START @ %s".format(this::class.java.simpleName, id, Thread.currentThread().name))
        preMainPhase()
        mainPhase()
        postMainPhase()
        linksPhase()
        logger.log(level, "%s #%s END".format(this::class.java.simpleName, id))
        return id
    }

    private fun preMainPhase() {
        logger.log(level, id, "preMainPhase")
        chainStatus = ChainCallback.Status.QUEUED
    }

    private fun mainPhase() {
        logger.log(level, id, "mainPhase")
        chainStatus = ChainCallback.Status.IN_PROGRESS
        Thread.sleep(duration)
        result = id
    }

    private fun postMainPhase() {
        logger.log(level, id, "postMainPhase")
    }

    private fun linksPhase() {
        logger.log(level, id, "linksPhase")
        if (links.isNotEmpty()) {
            logger.log(level, id, "%s is starting [%s] sub-chains".format(id, links.size))
            val internalExecutor: ExecutorService = when (linksExecutionStrategy) {
                ExecutionStrategy.SERIAL -> Executors.newSingleThreadExecutor()
                ExecutionStrategy.PARALLEL -> Executors.newCachedThreadPool()
            }
            val completionService: ExecutorCompletionService<TaskId> = ExecutorCompletionService(internalExecutor)

            links.forEach {
                logger.log(level, id, "%s is submitting [%s]".format(id, it.id))
                completionService.submit(it)
            }

            links.forEach {
                val future = completionService.take()
                future.get()
            }
        } else {
            logger.log(level, id, "%s has no sub-chains".format(id))
            reactionsPhase()
        }
    }

    private fun reactionsPhase() {
        logger.log(level, id, "reactionsPhase")
        runReactions()
        decisionPhase()
    }

    private fun decisionPhase() {
        logger.log(level, id, "decisionPhase")
        val incompleteLinks = links.count { it.chainStatus !in listOf(ChainCallback.Status.SUCCESS, ChainCallback.Status.ERROR) }
        logger.log(level, id, "decisionPhase links[%s] incomplete[%s]".format(links.size, incompleteLinks))

        if (links.isEmpty()) {
            chainStatus = decisionLogic(result, links)  //TODO: DETERMINE FINAL STATUS
            logger.log(level, id, "no chains to wait for, decision done")
            finishPhase()
        } else {
            if (incompleteLinks == 0) {
                chainStatus = decisionLogic(result, links)  //TODO: DETERMINE FINAL STATUS
                logger.log(level, id, "received all results, decision done")
                finishPhase()
            } else {
                // ALL LINKS IN_PROGRESS BUT SOME STILL MISSING RESULT (WAITING FOR ALL Chain Links TO OBTAIN RESULT)
                logger.log(level, id, "waiting for all results, decision pending")
            }
        }
    }

    //TODO: DELEGATE TO Reactor
    private fun decisionLogic(mainTaskResult: Any?, links: List<TaskAsCallable>): ChainCallback.Status {
        if (mainTaskResult != null && links.count { it.chainStatus == ChainCallback.Status.ERROR } == 0) {
            return ChainCallback.Status.SUCCESS
        }
        return ChainCallback.Status.ERROR
    }

    private fun finishPhase() {
        logger.log(level, id, "finishPhase")
        parentCallback.onDone(this)
    }

    /* ************** */
    /* FOR OVERRIDING */
    /* ************** */
    @Suppress("MemberVisibilityCanBePrivate")
    fun runReactions() {
    }
}