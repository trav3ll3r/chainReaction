package au.com.beba.chainReaction.feature.reactors

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

    fun startChain(callback: ChainCallback)

    fun preMainTaskPhase()
    fun mainTaskPhase()
    fun postMainTaskPhase()
    fun getChainTask(): ChainTask

    fun getChainResult(): Any?
    fun getChainStatus(): ChainCallback.Status
    fun setChainStatus(newStatus: ChainCallback.Status)
}

interface ChainWithPhases : Chain {
    fun addReaction(reaction: Reaction)

    // PHASES
    fun linksPhase()

    fun reactionsPhase()
    fun decisionPhase()
}

interface ChainDecisionListener {
    fun onDecisionDone(finalStatus: ChainCallback.Status)
    fun onDecisionNotDone()
}

interface ChainDecision {
    fun decision(links: List<Chain>, chain : ChainDecisionListener)
}