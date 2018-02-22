package au.com.beba.chainreaction.chain

import au.com.beba.chainreaction.logger.ConsoleLogger
import au.com.beba.chainreaction.reactor.BaseReactorWithPhases
import java.util.concurrent.ExecutorCompletionService
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

abstract class BaseChain(override val reactor: Reactor = BaseReactorWithPhases()) :
        AbstractChain(reactor),
        ChainWithReactions {

    @Suppress("PropertyName")
    override val TAG: String = BaseChain::class.java.simpleName

    //    private var linksExecutionStrategy: ExecutionStrategy = ExecutionStrategy.SERIAL
    private val links: MutableList<Chain> = mutableListOf()
    private val chainReactions: MutableList<Reaction> = mutableListOf()
    override val reactions: MutableList<Reaction>
        get() = chainReactions

    private lateinit var parentChainCallback: ChainCallback<Chain>

    init {
        addReaction(Reaction("LOGGER", {
            ConsoleLogger.log(TAG, "{%s} %s result=%s".format("REACTION", "LOGGER", getChainResult()))
        }))
    }

    override fun addToChain(vararg chainLinks: Chain): Chain {
        chainLinks.forEach {
            it.setParentCallback(childChainCallback)
            links.add(it)
        }
        return this
    }

    final override fun addReaction(reaction: Reaction) {
        chainReactions.add(reaction)
    }

    override fun getChainLinks(): List<Chain> {
        return links
    }

    override fun setParentCallback(parentCallback: ChainCallback<Chain>): Chain {
        this.parentChainCallback = parentCallback
        return this
    }

    override fun call(): Any? {
        ConsoleLogger.log(TAG, "%s START @ %s".format(this::class.java.simpleName, Thread.currentThread().name))
        preMainTaskPhase()
        mainTaskPhase()
        postMainTaskPhase()
        linksPhase()
        ConsoleLogger.log(TAG, "%s END".format(this::class.java.simpleName))
        return getChainResult()
    }

    /**
     * Calls [preMainTask] and then [mainTaskPhase]
     */
    private fun preMainTaskPhase() {
        ConsoleLogger.log(TAG, "preMainTaskPhase")
        setChainStatus(ChainCallback.Status.QUEUED)
        preMainTask()
    }

    override fun preMainTask() {
        ConsoleLogger.log(TAG, "preMainTask")
    }

    /**
     * Calls [getChainTask] and then [postMainTaskPhase]
     */
    private fun mainTaskPhase() {
        // RUN ChainTask (MAIN TASK)
        ConsoleLogger.log(TAG, "mainTaskPhase | run MainTask")
        setChainStatus(ChainCallback.Status.IN_PROGRESS)
        getChainTask().run(object : ChainTask.ChainTaskCallback {
            override fun onResult(task: ChainTask, newMainTaskStatus: ChainCallback.Status, taskResult: Any?) {
                ConsoleLogger.log(TAG, "mainTaskPhase:parentChainCallback | onResult | taskResult=%s".format(taskResult))
                setChainResult(taskResult)
                chainMainTaskStatus = newMainTaskStatus
                ConsoleLogger.log(TAG, "mainTaskPhase:parentChainCallback | onResult | end")
            }
        })
    }

    /* ******************** */
    /* POST-MAIN TASK PHASE */
    /* ******************** */
    private fun postMainTaskPhase() {
        ConsoleLogger.log(TAG, "postMainTaskPhase")
        postMainTask()
    }

    override fun postMainTask() {
        ConsoleLogger.log(TAG, "postMainTask")
    }

    /* ***************** */
    /* CHAIN LINKS PHASE */
    /* ***************** */
    private val childChainCallback = object : ChainCallback<Chain> {
        override fun onDone(completedChain: Chain) {
            ConsoleLogger.log(TAG, "childChainCallback | onDone | start")
            reactionsPhase()
            ConsoleLogger.log(TAG, "childChainCallback | onDone | finish")
        }
    }

    private fun linksPhase() {
        ConsoleLogger.log(TAG, "linksPhase")

        if (links.isNotEmpty()) {
            ConsoleLogger.log(TAG, "is starting [%s] sub-chains".format(links.size))
            val internalExecutor: ExecutorService = when (reactor.executionStrategy) {
                ExecutionStrategy.SERIAL -> Executors.newSingleThreadExecutor()
                ExecutionStrategy.PARALLEL -> Executors.newCachedThreadPool()
            }
            val completionService: ExecutorCompletionService<Any?> = ExecutorCompletionService(internalExecutor)

            links.forEach {
                ConsoleLogger.log(TAG, "is submitting [%s]".format(it::class.java.simpleName))
                completionService.submit(it)
            }

            links.forEach {
                val future = completionService.take()
                future.get()
            }
        } else {
            ConsoleLogger.log(TAG, "has no sub-chains")
            reactionsPhase()
        }
    }

    /* *************** */
    /* REACTIONS PHASE */
    /* *************** */
    /**
     * Runs all Reactions for this Chain
     */
    private fun reactionsPhase() {
        ConsoleLogger.log(TAG, "reactionsPhase")
        runReactions()
        decisionPhase()
    }

    override fun runReactions() {
        reactions.forEach { it.task.invoke(this) }
    }

    /* ************** */
    /* DECISION PHASE */
    /* ************** */
    private fun decisionPhase() {
        ConsoleLogger.log(TAG, "decisionPhase")
        val incompleteLinks = links.count { it.getChainStatus() !in listOf(ChainCallback.Status.SUCCESS, ChainCallback.Status.ERROR) }
        ConsoleLogger.log(TAG, "decisionPhase links[%s] incomplete[%s]".format(links.size, incompleteLinks))

        if (links.isEmpty()) {
            setChainStatus(decisionLogic())  //DETERMINE FINAL STATUS
            ConsoleLogger.log(TAG, "no chains to wait for, decision done")
            finishPhase()
        } else {
            if (incompleteLinks == 0) {
                setChainStatus(decisionLogic())  //DETERMINE FINAL STATUS
                ConsoleLogger.log(TAG, "received all results, decision done")
                finishPhase()
            } else {
                // ALL LINKS IN_PROGRESS BUT SOME STILL MISSING RESULT (WAITING FOR ALL Chain Links TO OBTAIN RESULT)
                ConsoleLogger.log(TAG, "waiting for all results, decision pending")
            }
        }
    }

    /**
     * Delegates implementation to the Reactor
     */
    private fun decisionLogic(): ChainCallback.Status {
        return reactor.chainDecision.decision(links, chainMainTaskStatus)
    }

//    override fun onDecisionDone(finalStatus: ChainCallback.Status) {
//        val decisionTag = "DECISION_DONE"
//        ConsoleLogger.log(TAG, "{%s} all links have result".format(decisionTag))
//
//        ConsoleLogger.log(TAG, "{%s} setting status [%s]".format(decisionTag, finalStatus))
//        setChainStatus(finalStatus)
//
//        chainFinishing()
//
//        // NOTIFY PARENT parentChainCallback
//        parentChainCallback.onDone(this)
//    }

//    override fun onDecisionNotDone() {
//        val decisionTag = "DECISION_NOT_DONE"
//        // RUN NEXT NOT_STARTED LINK
//        val nextLinks = links.filter { it.getChainStatus() == ChainCallback.Status.NOT_STARTED }
//        if (nextLinks.isNotEmpty()) {
//            linksPhase()
//        } else {
//            // ALL LINKS IN_PROGRESS BUT SOME STILL MISSING RESULT (WAITING FOR ALL Chain Links TO OBTAIN RESULT)
//            ConsoleLogger.log(TAG, "{%s} %s links without result, waiting for all".format(decisionTag, nextLinks.size))
//        }
//    }

    private fun finishPhase() {
        ConsoleLogger.log(TAG, "finishPhase")
        chainFinishing()
        parentChainCallback.onDone(this)
    }

    override fun chainFinishing() {
        ConsoleLogger.log(TAG, "chainFinishing")
    }
}