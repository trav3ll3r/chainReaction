package au.com.beba.phaserizer.feature.reactors

import au.com.beba.phaserizer.feature.ConsoleLogger

abstract class BaseChain(
    private val reactor: Reactor = DefaultReactor()
) : Chain, ChainDecisionListener {
    protected open val TAG = BaseChain::class.java.simpleName

    private var result: Any? = null
    private var status = ChainCallback.Status.NOT_STARTED
    private val links: MutableList<Chain> = mutableListOf()
    private val reactions: MutableList<Reaction> = mutableListOf()

    private lateinit var chainCallback: ChainCallback

    init {
        reactions.add(Reaction(type = "LOGGER", task = {
            ConsoleLogger.log(TAG, "{%s} %s result=%s".format("REACTION", "LOGGER", result))
        }))
    }

    override fun onDecisionDone(finalStatus: ChainCallback.Status) {
        // NOTIFY PARENT chainCallback
        chainCallback.onDone(finalStatus)
    }

    override fun onDecisionNext() {
        val decisionTag = "FINISH_OR_LINKS"
        // RUN NEXT NOT_STARTED LINK
        val nextLink = links.find { it.getChainStatus() == ChainCallback.Status.NOT_STARTED }
        if (nextLink != null) {
            ConsoleLogger.log(TAG, "{%s} starting not started Link %s".format(decisionTag, nextLink.javaClass.simpleName))
            nextLink.startChain(childChainCallback)
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

    override fun getChainResult(): Any? {
        //ConsoleLogger.log(TAG, "{%s} getChainResult | taskResult=%s".format("CHAIN", result))
        return result
    }

    private fun setChainResult(value: Any?) {
        ConsoleLogger.log(TAG, "setChainResult | taskResult=%s".format(value))
        result = value
    }

    override fun getChainStatus(): ChainCallback.Status {
        //ConsoleLogger.log(TAG, "getChainStatus | status=%s".format(status))
        return this.status
    }

    override fun setChainStatus(newStatus: ChainCallback.Status) {
        ConsoleLogger.log(TAG, "setChainStatus | status=%s => %s".format(this.status, newStatus))
        this.status = newStatus
    }

    override fun startChain(callback: ChainCallback) {
        ConsoleLogger.log(TAG, "startChain")
        chainCallback = callback

        // RUN ChainTask (MAIN TASK)
        ConsoleLogger.log(TAG, "startChain | run MainTask")
        setChainStatus(ChainCallback.Status.IN_PROGRESS)
        getChainTask().run(object : ChainTask.ChainTaskCallback {
            override fun onResult(task: ChainTask, newStatus: ChainCallback.Status, taskResult: Any?) {
                ConsoleLogger.log(TAG, "chainCallback: onResult | task=%s | taskResult=%s".format(task::class.java.simpleName, taskResult))
                setChainResult(taskResult)
                setChainStatus(newStatus)
            }
        })

        runReactions()
    }

    private val childChainCallback = object : ChainCallback {
        override fun onDone(status: ChainCallback.Status) {
            ConsoleLogger.log(TAG, "childChainCallback | onDone | start")

            runReactions()

            ConsoleLogger.log(TAG, "childChainCallback | onDone | finish")
        }
    }

    /**
     * Runs all Reactions for this Chain
     */
    private fun runReactions() {
        ConsoleLogger.log(TAG, "runReactions")
        reactions()
        decision()
    }

    override fun decision() {
        reactor.chainDecision.decision(links, this)
    }

    override fun reactions() {
        reactions.forEach { it.task.invoke(Unit) }
    }
}

class BaseChainDecision : ChainDecision {
    private val TAG = BaseChainDecision::class.java.simpleName
    override fun decision(links: List<Chain>, chain : ChainDecisionListener) {
        val decisionTag = "DECISION"
        ConsoleLogger.log(TAG, "{%s}".format(decisionTag))

        //TODO: CHECK "END" CONDITIONS
        val unfinishedLinks = links.count { it.getChainStatus() in listOf(ChainCallback.Status.NOT_STARTED, ChainCallback.Status.IN_PROGRESS) }

        if (unfinishedLinks == 0) {
            ConsoleLogger.log(TAG, "{%s} %s".format(decisionTag, "all chain links have a result"))
            val finalStatus = ChainCallback.Status.SUCCESS // TODO: CALCULATE
            ConsoleLogger.log(TAG, "{%s} notifying parent via chainCallback".format(decisionTag))
            // NOTIFY PARENT chainCallback
            chain.onDecisionDone(finalStatus)
        } else {
            ConsoleLogger.log(TAG, "{%s} not all Links have result yet".format(decisionTag))
            chain.onDecisionNext()
        }
    }
}