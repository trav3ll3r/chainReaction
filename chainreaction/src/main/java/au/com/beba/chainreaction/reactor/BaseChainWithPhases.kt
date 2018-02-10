package au.com.beba.chainreaction.reactor

import au.com.beba.chainreaction.chain.*
import au.com.beba.chainreaction.logger.ConsoleLogger

abstract class BaseChainWithPhases(private val reactor: Reactor = BaseReactor()) :
        AbstractChain(reactor),
        ChainWithPhases,
        ChainDecisionListener {

    @Suppress("PropertyName")
    override val TAG: String = BaseChainWithPhases::class.java.simpleName

    private val links: MutableList<Chain> = mutableListOf()
    private val reactions: MutableList<Reaction> = mutableListOf()

    private lateinit var chainCallback: ChainCallback

    init {
        reactions.add(Reaction(type = "LOGGER", task = {
            ConsoleLogger.log(TAG, "{%s} %s result=%s".format("REACTION", "LOGGER", getChainResult()))
        }))
    }

    override fun onDecisionDone(finalStatus: ChainCallback.Status) {
        // NOTIFY PARENT chainCallback
        chainCallback.onDone(finalStatus)
    }

    override fun onDecisionNotDone() {
        val decisionTag = "DECISION_NEXT"
        // RUN NEXT NOT_STARTED LINK
        val nextLink = links.find { it.getChainStatus() == ChainCallback.Status.NOT_STARTED }
        if (nextLink != null) {
            linksPhase()
        } else {
            // ALL LINKS IN_PROGRESS BUT SOME STILL MISSING RESULT (WAITING FOR ALL Chain Links TO OBTAIN RESULT)
            ConsoleLogger.log(TAG, "{%s} %s links without result, waiting for all".format(decisionTag, /*unfinishedLinks*/"???"))
        }
    }

    final override fun addToChain(chain: Chain) {
        links.add(chain)
    }

    final override fun addReaction(reaction: Reaction) {
        reactions.add(reaction)
    }

    override fun startChain(callback: ChainCallback) {
        ConsoleLogger.log(TAG, "startChain")
        chainCallback = callback

        preMainTaskPhase()
    }

    private val childChainCallback = object : ChainCallback {
        override fun onDone(status: ChainCallback.Status) {
            ConsoleLogger.log(TAG, "childChainCallback | onDone | start")
            reactionsPhase()
            ConsoleLogger.log(TAG, "childChainCallback | onDone | finish")
        }
    }

    override fun preMainTaskPhase() {
        ConsoleLogger.log(TAG, "preMainTaskPhase")
        mainTaskPhase()
    }

    override fun mainTaskPhase() {
        // RUN ChainTask (MAIN TASK)
        ConsoleLogger.log(TAG, "startChain | run MainTask")
        setChainStatus(ChainCallback.Status.IN_PROGRESS)
        getChainTask().run(object : ChainTask.ChainTaskCallback {
            override fun onResult(task: ChainTask, newStatus: ChainCallback.Status, taskResult: Any?) {
                ConsoleLogger.log(TAG, "chainCallback: onResult | taskResult=%s".format(taskResult))
                setChainResult(taskResult)
                setChainStatus(newStatus)

                postMainTaskPhase()
            }
        })
    }

    override fun postMainTaskPhase() {
        ConsoleLogger.log(TAG, "postMainTaskPhase")
        reactionsPhase()
    }

    override fun linksPhase() {
        val linksPhaseTag = "linksPhase"
        val nextLink = links.find { it.getChainStatus() == ChainCallback.Status.NOT_STARTED }
        ConsoleLogger.log(TAG, "{%s} starting not started Link %s".format(linksPhaseTag, nextLink?.javaClass?.simpleName
                ?: ""))
        nextLink?.startChain(childChainCallback)
    }

    /**
     * Runs all Reactions for this Chain
     */
    override fun reactionsPhase() {
        ConsoleLogger.log(TAG, "reactionsPhase")
        reactions.forEach { it.task.invoke(Unit) }

        decisionPhase()
    }

    override fun decisionPhase() {
        ConsoleLogger.log(TAG, "decisionPhase")
        reactor.chainDecision.decision(links, this)
    }
}