package au.com.beba.chainreaction.chain

import au.com.beba.chainreaction.logger.ConsoleLogger
import au.com.beba.chainreaction.reactor.BaseReactorWithPhases
import org.jetbrains.anko.doAsync
import java.util.concurrent.Executor
import java.util.concurrent.Executors

abstract class BaseChain(override val reactor: Reactor = BaseReactorWithPhases()) :
        AbstractChain(reactor),
        ChainWithDecision,
        ChainDecisionListener,
        ChainWithReactions {

    @Suppress("PropertyName")
    override val TAG: String = BaseChain::class.java.simpleName

    private val links: MutableList<Chain> = mutableListOf()
    private val chainReactions: MutableList<Reaction> = mutableListOf()
    override val reactions: MutableList<Reaction>
        get() = chainReactions

    private lateinit var chainCallback: ChainCallback

    init {
        addReaction(Reaction("LOGGER", {
            ConsoleLogger.log(TAG, "{%s} %s result=%s".format("REACTION", "LOGGER", getChainResult()))
        }))
    }

    override fun addToChain(vararg chainLinks: Chain): Chain {
        links.addAll(chainLinks)
        return this
    }

    final override fun addReaction(reaction: Reaction) {
        chainReactions.add(reaction)
    }

    override fun getChainLinks(): List<Chain> {
        return links
    }

    override fun startChain(callback: ChainCallback) {
        ConsoleLogger.log(TAG, "startChain")
        chainCallback = callback

        val chainExecutor: Executor = Executors.newSingleThreadExecutor()
        chainExecutor.doAsync { preMainTaskPhase() }
    }

    override fun startChainOnSameThread(callback: ChainCallback) {
        ConsoleLogger.log(TAG, "startChainOnSameThread")
        chainCallback = callback

        val chainExecutor: Executor = Executors.newSingleThreadExecutor()
        val f = chainExecutor.doAsync { preMainTaskPhase() }
        f.get()
    }

    private fun startSubChain(callback: ChainCallback): () -> Any? {
        ConsoleLogger.log(TAG, "startSubChain")
        chainCallback = callback
        preMainTaskPhase()
        return {}
    }

    /**
     * Calls [preMainTask] and then [mainTaskPhase]
     */
    private fun preMainTaskPhase(): () -> Any? {
        ConsoleLogger.log(TAG, "preMainTaskPhase")
        setChainStatus(ChainCallback.Status.IN_PROGRESS)
        preMainTask()
        return mainTaskPhase()
    }

    override fun preMainTask() {
        ConsoleLogger.log(TAG, "preMainTask")
    }

    /**
     * Calls [getChainTask] and then [postMainTaskPhase]
     */
    private fun mainTaskPhase(): () -> Any? {
        // RUN ChainTask (MAIN TASK)
        ConsoleLogger.log(TAG, "mainTaskPhase | run MainTask")
        getChainTask().run(object : ChainTask.ChainTaskCallback {
            override fun onResult(task: ChainTask, newMainTaskStatus: ChainCallback.Status, taskResult: Any?) {
                ConsoleLogger.log(TAG, "mainTaskPhase:chainCallback | onResult | taskResult=%s".format(taskResult))
                setChainResult(taskResult)
                chainMainTaskStatus = newMainTaskStatus
                ConsoleLogger.log(TAG, "mainTaskPhase:chainCallback | onResult | end")
            }
        })
        ConsoleLogger.log(TAG, "mainTaskPhase | call postMainTaskPhase")
        return postMainTaskPhase()
    }

    /* ******************** */
    /* POST-MAIN TASK PHASE */
    /* ******************** */
    private fun postMainTaskPhase(): () -> Any? {
        ConsoleLogger.log(TAG, "postMainTaskPhase")
        postMainTask()
        return decisionPhase()
    }

    override fun postMainTask() {
        ConsoleLogger.log(TAG, "postMainTask")
    }

    /* ************** */
    /* DECISION PHASE */
    /* ************** */
    override fun decisionPhase(): () -> Any? {
        ConsoleLogger.log(TAG, "decisionPhase")
        reactor.chainDecision.decision(links, chainMainTaskStatus, this)
        return {}
    }

    override fun onDecisionDone(finalStatus: ChainCallback.Status) {
        val decisionTag = "DECISION_DONE"
        ConsoleLogger.log(TAG, "{%s} all links have result".format(decisionTag))

        ConsoleLogger.log(TAG, "{%s} setting status [%s]".format(decisionTag, finalStatus))
        setChainStatus(finalStatus)

        chainFinished()

        // NOTIFY PARENT chainCallback
        chainCallback.onDone(finalStatus)
    }

    override fun onDecisionNotDone() {
        val decisionTag = "DECISION_NOT_DONE"
        // RUN NEXT NOT_STARTED LINK
        val nextLink = links.find { it.getChainStatus() == ChainCallback.Status.NOT_STARTED }
        if (nextLink != null) {
            linksPhase()
        } else {
            // ALL LINKS IN_PROGRESS BUT SOME STILL MISSING RESULT (WAITING FOR ALL Chain Links TO OBTAIN RESULT)
            ConsoleLogger.log(TAG, "{%s} %s links without result, waiting for all".format(decisionTag, /*unfinishedLinks*/"???"))
        }
    }

    override fun chainFinished() {
        ConsoleLogger.log(TAG, "chainFinished")
    }

    /* ***************** */
    /* CHAIN LINKS PHASE */
    /* ***************** */
    private val childChainCallback = object : ChainCallback {
        override fun onDone(status: ChainCallback.Status) {
            ConsoleLogger.log(TAG, "childChainCallback | onDone | start")
            reactionsPhase()
            ConsoleLogger.log(TAG, "childChainCallback | onDone | finish")
        }
    }

    override fun linksPhase(): () -> Any? {
        val linksPhaseTag = "LINKS"
        val notStartedLinks = links.filter { it.getChainStatus() in listOf(ChainCallback.Status.NOT_STARTED) }
//        if (nextLink != null) {
//            ConsoleLogger.log(TAG, "{%s} starting Link %s".format(linksPhaseTag, nextLink.javaClass.simpleName))
//            nextLink.startChain(childChainCallback)
//        }

//        links.forEach { reactor.chainExecutor.submit( { it.startChain(childChainCallback) } )}
//        links.forEach { reactor.chainExecutor.submit( { (it as BaseChain).startSubChain(childChainCallback) } )}
        notStartedLinks.forEach { reactor.chainExecutor.submit( (it as BaseChain).startSubChain(childChainCallback) )}

        return {}
    }

    /* *************** */
    /* REACTIONS PHASE */
    /* *************** */
    /**
     * Runs all Reactions for this Chain
     */
    private fun reactionsPhase(): () -> Any? {
        ConsoleLogger.log(TAG, "reactionsPhase")
        runReactions()
        return decisionPhase()
    }

    override fun runReactions() {
        reactions.forEach { it.task.invoke(this) }
    }
}