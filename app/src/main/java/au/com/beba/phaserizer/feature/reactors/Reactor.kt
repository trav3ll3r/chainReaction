package au.com.beba.phaserizer.feature.reactors

//import java.util.concurrent.Executor
//import java.util.concurrent.Executors

//object ChainConfig {
////    var executor: Executor = Executors.newSingleThreadExecutor()
////    var executor: Executor = Executors.newCachedThreadPool()
//    var executor: Executor = Executors.newFixedThreadPool(1)
//    var critical: Boolean = false
//}

//interface Reactor {
//    fun react(reactions: List<Reaction>)
//    fun continueOrFinish()
//}

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
    interface ChainTaskCallback {
        fun onResult(task: ChainTask, newStatus: ChainCallback.Status, taskResult: Any?)
    }

    fun run(callback: ChainTaskCallback)
}

class Reaction(val type: String, val task: (Any?) -> (Any?))

interface Chain {
    fun addToChain(chain: Chain)
    fun addReaction(reaction: Reaction)

    fun startChain(callback: ChainCallback)
    fun getChainTask(): ChainTask
    fun reactions()
    fun decision()

    fun getChainResult(): Any?
    fun getChainStatus(): ChainCallback.Status
    fun setChainStatus(newStatus: ChainCallback.Status)
}

interface ChainDecisionListener {
    fun onDecisionDone(finalStatus: ChainCallback.Status)
    fun onDecisionNext()
}

interface ChainDecision {
    fun decision(links: List<Chain>, chain : ChainDecisionListener)
}