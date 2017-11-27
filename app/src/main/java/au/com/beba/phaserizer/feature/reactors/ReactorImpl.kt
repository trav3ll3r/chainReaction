package au.com.beba.phaserizer.feature.reactors

import android.util.Log

abstract class BaseChainReaction(private val reactor: Reactor = DefaultReactor()) : ChainReaction {
    companion object {
        val TAG = BaseChainReaction::class.java.simpleName
    }

    private var result: Any? = null
    private val links: MutableList<ChainReaction> = mutableListOf()
    private val reactions: MutableList<Reaction> = mutableListOf()

    init {
        reactions.add(Reaction(type = "LOGGER", task = { Log.d("TAG", "") }))
    }

    final override fun addToChain(chain: ChainReaction) {
        links.add(chain)
    }

    final override fun addReaction(reaction: Reaction) {
        reactions.add(reaction)
    }

    override fun getReactionResult(): Any? {
        println("reactor: getReactionResult | taskResult=%s".format(result))
        return result
    }

    private fun setReactionResult(value: Any?) {
        println("setReactionResult: task=%s | taskResult=%s".format(this::class.java.simpleName, value))
        result = value
    }

    private val reactorCallback = object : RectorCallback {
        override fun onResult(task: ChainTask, status: ChainTaskCallback.Status, taskResult: Any?) {
            println("reactorCallback: onResult | task=%s | taskResult=%s".format(task::class.java.simpleName, taskResult))
            setReactionResult(taskResult)
            callback.onTaskCompleted(status)
        }
    }

    private val callback = object : ChainTaskCallback {
        override fun onTaskCompleted(status: ChainTaskCallback.Status) {
            println("onTaskCompleted")
        }
    }

    override fun startReaction() {
        // RUN Reactor TASK
        getLinkTask().run(reactorCallback)

        links.forEach {
            it.startReaction()
        }

        reactor.react(reactions)
    }
}