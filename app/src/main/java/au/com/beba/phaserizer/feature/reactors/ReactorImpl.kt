package au.com.beba.phaserizer.feature.reactors

import au.com.beba.phaserizer.feature.ConsoleLogger

abstract class BaseChainReaction(private val reactor: Reactor = DefaultReactor()) : ChainReaction {
    protected open val TAG = BaseChainReaction::class.java.simpleName

    private var result: Any? = null
    private var status = ChainReactionCallback.Status.NOT_STARTED
    private val links: MutableList<ChainReaction> = mutableListOf()
    private val reactions: MutableList<Reaction> = mutableListOf()

    private lateinit var chainReactionCallback: ChainReactionCallback

    init {
        reactions.add(Reaction(type = "LOGGER", task = {
            ConsoleLogger.log(TAG, "{%s} %s".format("REACTION", "LOGGER"))
        }))
    }

    final override fun addToChain(chain: ChainReaction) {
        links.add(chain)
    }

    final override fun addReaction(reaction: Reaction) {
        reactions.add(reaction)
    }

    override fun getChainResult(): Any? {
        ConsoleLogger.log(TAG, "{%s} getChainResult | taskResult=%s".format("CHAIN", result))
        return result
    }

    private fun setChainResult(value: Any?) {
        ConsoleLogger.log(TAG, "{%s} setChainResult: task=%s | taskResult=%s".format("CHAIN", this::class.java.simpleName, value))
        result = value
    }

    override fun getChainStatus(): ChainReactionCallback.Status {
        ConsoleLogger.log(TAG, "{%s} getChainStatus | status=%s".format("CHAIN", status))
        return this.status
    }

    override fun setChainStatus(status: ChainReactionCallback.Status) {
        ConsoleLogger.log(TAG, "{%s} setChainStatus | status=%s".format("CHAIN", status))
        this.status = status
    }

//    private val reactorTaskCallback = object : ChainTask.ReactorTaskCallback {
//        override fun onResult(task: ChainTask, status: ChainReactionCallback.Status, taskResult: Any?) {
//            ConsoleLogger.log("{%s} reactorTaskCallback: onResult | task=%s | taskResult=%s".format("CHAIN-REACTION", task::class.java.simpleName, taskResult))
//            setChainResult(taskResult)
//        }
//    }

    override fun startReaction(callback: ChainReactionCallback) {
        ConsoleLogger.log(TAG, "{%s} startReaction".format("CHAIN"))
        chainReactionCallback = callback
        // RUN Reactions's TASK
        getLinkTask().run(object : ChainTask.ReactorTaskCallback {
            override fun onResult(task: ChainTask, status: ChainReactionCallback.Status, taskResult: Any?) {
                ConsoleLogger.log(TAG, "{%s} chainReactionCallback: onResult | task=%s | taskResult=%s".format("CHAIN", task::class.java.simpleName, taskResult))
                setChainResult(taskResult)
            }
        })

        // RUN Reactor Links TASKS
        if (links.isEmpty()) {
            internalChainReactionCallback.onDone(ChainReactionCallback.Status.SUCCESS)
        } else {
            links.forEach {
                it.startReaction(internalChainReactionCallback)
            }
        }
    }

    private val internalChainReactionCallback = object : ChainReactionCallback {
        override fun onDone(status: ChainReactionCallback.Status) {
            ConsoleLogger.log(TAG, "{%s} internalChainReactionCallback | onDone | start".format("CHAIN"))
            //RUN REACTIONS
            ConsoleLogger.log(TAG, "{%s} internalChainReactionCallback | onDone | run reactions".format("CHAIN"))
            reactor.react(reactions)

            //TODO: CHECK "END" CONDITIONS
            if (links.count { it.getChainResult() == null } == 0) {
                ConsoleLogger.log(TAG, "{%s} internalChainReactionCallback | onDone | all chain links have a result".format("CHAIN"))
                //TODO: CALL PARENT chainReactionCallback
                val finalStatus = ChainReactionCallback.Status.SUCCESS
                chainReactionCallback.onDone(finalStatus)
            } else {
                // WAIT FOR ALL Chain Links TO REPORT BACK (OBTAIN RESULT)
                ConsoleLogger.log(TAG, "{%s} internalChainReactionCallback | onDone | waiting for all chain links to obtain result".format("CHAIN"))
            }

            ConsoleLogger.log(TAG, "{%s} internalChainReactionCallback | onDone | finish".format("CHAIN"))
        }
    }
}