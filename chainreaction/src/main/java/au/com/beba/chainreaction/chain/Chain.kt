package au.com.beba.chainreaction.chain

import java.util.concurrent.Callable

interface Chain : Callable<Any?> {
    val reactor: Reactor
    fun addToChain(vararg chainLinks: Chain): Chain

    fun setParentCallback(parentCallback: ChainCallback<Chain>): Chain

    // PHASES
    fun preMainTask()
    fun getChainTask(): ChainTask
    fun postMainTask()
    fun chainFinishing()

    fun getChainLinks(): List<Chain>
    fun getChainResult(): Any?
    fun getMainTaskStatus(): ChainCallback.Status
    fun getChainStatus(): ChainCallback.Status
    fun setChainStatus(newStatus: ChainCallback.Status)
}

enum class ExecutionStrategy {
    SERIAL,
    PARALLEL
}