package au.com.beba.phaserizer.feature.reactors

//import java.util.concurrent.Executor
//import java.util.concurrent.Executors

//object ChainConfig {
////    var executor: Executor = Executors.newSingleThreadExecutor()
////    var executor: Executor = Executors.newCachedThreadPool()
//    var executor: Executor = Executors.newFixedThreadPool(1)
//    var critical: Boolean = false
//}

interface Reactor {
    fun react(reactions: List<Reaction>)
}

interface ChainReactionCallback {
    fun onDone(status: ChainReactionCallback.Status)

    enum class Status {
        NOT_STARTED,
        ERROR,
        SUCCESS
    }
}

interface ChainTask {
    interface ReactorTaskCallback {
        fun onResult(task: ChainTask, status: ChainReactionCallback.Status, taskResult: Any?)
    }

    fun run(callback: ReactorTaskCallback)
}

class Reaction(val type: String, val task: (Any?) -> (Any?))

interface ChainReaction {
    fun addToChain(chain: ChainReaction)
    fun addReaction(reaction: Reaction)
    fun getLinkTask(): ChainTask
    fun startReaction(callback: ChainReactionCallback)
    fun getChainResult(): Any?
    fun getChainStatus(): ChainReactionCallback.Status
    fun setChainStatus(status: ChainReactionCallback.Status)
}

open class DefaultReactor : Reactor {
    override fun react(reactions: List<Reaction>) {
        reactions.forEach { it.task.invoke(Unit) }
    }
}