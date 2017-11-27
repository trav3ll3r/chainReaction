package au.com.beba.phaserizer.feature.reactors

import au.com.beba.phaserizer.feature.ConsoleLogger

abstract class BaseChainReaction(private val reactor: Reactor = DefaultReactor()) : ChainReaction {
    companion object {
        val TAG = BaseChainReaction::class.java.simpleName
    }

    private var result: Any? = null
    private val links: MutableList<ChainReaction> = mutableListOf()
    private val reactions: MutableList<Reaction> = mutableListOf()

    private lateinit var chainReactionCallback: ChainReactionCallback

    init {
        reactions.add(Reaction(type = "LOGGER", task = {
            ConsoleLogger.log("{%s} %s %s".format("REACTION", TAG, "LOGGER"))
        }))
    }

    final override fun addToChain(chain: ChainReaction) {
        links.add(chain)
    }

    final override fun addReaction(reaction: Reaction) {
        reactions.add(reaction)
    }

    override fun getReactionResult(): Any? {
        ConsoleLogger.log("{%s} getReactionResult | taskResult=%s".format("CHAIN-REACTION", result))
        return result
    }

    private fun setReactionResult(value: Any?) {
        ConsoleLogger.log("{%s} setReactionResult: task=%s | taskResult=%s".format("CHAIN-REACTION", this::class.java.simpleName, value))
        result = value
    }

    private val reactorCallback = object : ReactorCallback {
        override fun onResult(task: ChainTask, status: ChainReactionCallback.Status, taskResult: Any?) {
            ConsoleLogger.log("{%s} reactorCallback: onResult | task=%s | taskResult=%s".format("CHAIN-REACTION", task::class.java.simpleName, taskResult))
            setReactionResult(taskResult)
//            callback.onDone(status)
        }
    }

//    private val callback = object : ChainReactionCallback {
//        override fun onDone(status: ChainReactionCallback.Status) {
//            ConsoleLogger.log("{%s} onDone".format("CHAIN-REACTION"))
//        }
//    }

    override fun startReaction(callback: ChainReactionCallback) {
        chainReactionCallback = callback
        // RUN Reactor's TASK
        getLinkTask().run(reactorCallback)

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
            //RUN REACTIONS
            reactor.react(reactions)

            //TODO: CHECK "END" CONDITIONS
            if (links.count { it.getReactionResult() == null } == 0) {
                //TODO: CALL PARENT chainReactionCallback
                val finalStatus = ChainReactionCallback.Status.SUCCESS
                chainReactionCallback.onDone(finalStatus)
            } else {
                // WAIT FOR ALL LINKS TO REPORT BACK
            }
        }
    }
}