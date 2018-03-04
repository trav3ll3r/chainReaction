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

    private val links: MutableList<Chain> = mutableListOf()
    private val chainReactions: MutableList<Reaction> = mutableListOf()
    override val reactions: MutableList<Reaction>
        get() = chainReactions

    private lateinit var parentChainCallback: ChainCallback<Chain>
    private var completionService: ExecutorCompletionService<Any?>? = null

    init {
        addReaction(Reaction("LOGGER", { _, _ ->
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
        val notStartedLinks = links.filter { it.getChainStatus() in listOf(ChainCallback.Status.NOT_STARTED) }

        if (notStartedLinks.isNotEmpty()) {
            runNonStartedLinks()
        } else {
            ConsoleLogger.log(TAG, "has no sub-chains")
            reactionsPhase()
        }
    }

    private fun runNonStartedLinks() {
        val notStartedLinks = links.filter { it.getChainStatus() in listOf(ChainCallback.Status.NOT_STARTED) }

        if (notStartedLinks.isNotEmpty()) {
            ConsoleLogger.log(TAG, "is starting [%s] sub-chains".format(notStartedLinks.size))
            //if (completionService == null) {
            initExecutor()
            //}

            notStartedLinks.forEach {
                ConsoleLogger.log(TAG, "is submitting [%s]".format(it::class.java.simpleName))
                it.setChainStatus(ChainCallback.Status.QUEUED)
                completionService!!.submit(it)
            }

            notStartedLinks.forEach {
                val future = completionService!!.take()
                future.get()
            }
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
        reactions.forEach {
            if (!it.skip) {
                ConsoleLogger.log(TAG, "{%s} %s".format("REACTION", it.type))
                it.task.invoke(this, it)
            }
        }
    }

    /* ************** */
    /* DECISION PHASE */
    /* ************** */
    private fun decisionPhase() {
        ConsoleLogger.log(TAG, "decisionPhase")
        val incompleteLinks = links.count { it.getChainStatus() !in listOf(ChainCallback.Status.SUCCESS, ChainCallback.Status.ERROR) }
        val notStartedLinks = links.count { it.getChainStatus() in listOf(ChainCallback.Status.NOT_STARTED) }
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
                if (notStartedLinks == 0) {
                    // ALL LINKS IN_PROGRESS BUT SOME STILL MISSING RESULT (WAITING FOR ALL Chain Links TO OBTAIN RESULT)
                    ConsoleLogger.log(TAG, "waiting for all results, decision pending")
                } else {
                    // START NOT_STARTED LINKS
                    runNonStartedLinks()
                }
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

    private fun initExecutor() {
        val internalExecutor: ExecutorService = when (reactor.executionStrategy) {
            ExecutionStrategy.SERIAL -> Executors.newSingleThreadExecutor()
            ExecutionStrategy.PARALLEL -> Executors.newCachedThreadPool()
        }
        completionService = ExecutorCompletionService(internalExecutor)
    }
}