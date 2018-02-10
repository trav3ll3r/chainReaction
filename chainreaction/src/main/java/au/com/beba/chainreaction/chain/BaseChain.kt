package au.com.beba.chainreaction.chain

import au.com.beba.chainreaction.logger.ConsoleLogger
import au.com.beba.chainreaction.reactor.BaseReactorWithPhases
import org.jetbrains.anko.doAsync

abstract class BaseChain(private val reactor: Reactor = BaseReactorWithPhases()) :
        AbstractChain(reactor),
        ChainWithDecision,
        ChainDecisionListener,
        ChainWithReactions {

    @Suppress("PropertyName")
    override val TAG: String = BaseChain::class.java.simpleName

    private val links: MutableList<Chain> = mutableListOf()
    private val reactions: MutableList<Reaction> = mutableListOf()

    private lateinit var chainCallback: ChainCallback

    init {
        reactions.add(Reaction(type = "LOGGER", task = {
            ConsoleLogger.log(TAG, "{%s} %s result=%s".format("REACTION", "LOGGER", getChainResult()))
        }))
    }

    final override fun addToChain(link: Chain) {
        links.add(link)
    }

    final override fun addReaction(reaction: Reaction) {
        reactions.add(reaction)
    }

    override fun startChain(callback: ChainCallback) {
        ConsoleLogger.log(TAG, "startChain")
        chainCallback = callback

        val f = reactor.chainExecutor.doAsync { preMainTaskPhase() }
        f.get()
    }

    override fun preMainTaskPhase(): () -> Any? {
        ConsoleLogger.log(TAG, "preMainTaskPhase")
        return mainTaskPhase()
    }

    override fun mainTaskPhase(): () -> Any? {
        // RUN ChainTask (MAIN TASK)
        ConsoleLogger.log(TAG, "mainTaskPhase | run MainTask")
        setChainStatus(ChainCallback.Status.IN_PROGRESS)
        getChainTask().run(object : ChainTask.ChainTaskCallback {
            override fun onResult(task: ChainTask, newStatus: ChainCallback.Status, taskResult: Any?) {
                ConsoleLogger.log(TAG, "mainTaskPhase:chainCallback | onResult | taskResult=%s".format(taskResult))
                setChainResult(taskResult)
                setChainStatus(newStatus)
                ConsoleLogger.log(TAG, "mainTaskPhase:chainCallback | onResult | end")
            }
        })
        ConsoleLogger.log(TAG, "mainTaskPhase | call postMainTaskPhase")
        return postMainTaskPhase()
    }

    override fun postMainTaskPhase(): () -> Any? {
        ConsoleLogger.log(TAG, "postMainTaskPhase")
        return decisionPhase()
    }

    /* ************** */
    /* DECISION PHASE */
    /* ************** */
    override fun decisionPhase(): () -> Any? {
        ConsoleLogger.log(TAG, "decisionPhase")
        reactor.chainDecision.decision(links, this)
        return {}
    }

    override fun onDecisionDone(finalStatus: ChainCallback.Status) {
        val decisionTag = "DECISION_DONE"
        ConsoleLogger.log(TAG, "{%s} all links have result".format(decisionTag))
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
        val nextLink = links.find { it.getChainStatus() == ChainCallback.Status.NOT_STARTED }
        if (nextLink != null) {
            ConsoleLogger.log(TAG, "{%s} starting Link %s".format(linksPhaseTag, nextLink.javaClass.simpleName))
            nextLink.startChain(childChainCallback)
        }

        return {}
    }

    /* *************** */
    /* REACTIONS PHASE */
    /* *************** */
    /**
     * Runs all Reactions for this Chain
     */
    override fun reactionsPhase(): () -> Any? {
        ConsoleLogger.log(TAG, "reactionsPhase")
        reactions.forEach { it.task.invoke(Unit) }

        return decisionPhase()
    }

}