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

interface ChainTaskCallback {
    fun onTaskCompleted(status: Status)

    enum class Status {
        ERROR,
        SUCCESS
    }
}

interface RectorCallback {
    fun onResult(task: ChainTask, status: ChainTaskCallback.Status, taskResult: Any?)
}

interface ChainTask {
    fun run(callback: RectorCallback)
}

class Reaction(val type: String, val task: (Any?) -> (Any?))

interface ChainReaction {
    fun addToChain(chain: ChainReaction)
    fun addReaction(reaction: Reaction)
    fun getLinkTask(): ChainTask
    fun startReaction()
    fun getReactionResult(): Any?
}

open class DefaultReactor : Reactor {
    override fun react(reactions: List<Reaction>) {
        reactions.forEach { it.task }
    }
}