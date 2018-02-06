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

interface ChainCallback {
    fun onDone(status: ChainCallback.Status)

    enum class Status {
        NOT_STARTED,
        IN_PROGRESS,
        ERROR,
        SUCCESS
    }
}

interface ChainTask {
    interface ReactorTaskCallback {
        fun onResult(task: ChainTask, status: ChainCallback.Status, taskResult: Any?)
    }

    fun run(callback: ReactorTaskCallback)
}

class Reaction(val type: String, val task: (Any?) -> (Any?))

interface Chain {
    fun addToChain(chain: Chain)
    fun addReaction(reaction: Reaction)
    fun getChainTask(): ChainTask
    fun startChain(callback: ChainCallback)
    fun getChainResult(): Any?
    fun getChainStatus(): ChainCallback.Status
    fun setChainStatus(newStatus: ChainCallback.Status)
}

class DefaultReactor : Reactor {
    override fun react(reactions: List<Reaction>) {
        reactions.forEach { it.task.invoke(Unit) }
    }
}