package au.com.beba.chainreaction.chain

interface Chain {
    val reactor: Reactor
    fun addToChain(vararg chainLinks: Chain): Chain

    fun startChain(callback: ChainCallback<Chain>): () -> Any?
    fun startChainOnSameThread(callback: ChainCallback<Chain>)

    // PHASES
    fun preMainTask()
    fun getChainTask(): ChainTask
    fun postMainTask()
    fun linksPhase(): () -> Any?
    fun chainFinished()

    fun getChainLinks(): List<Chain>
    fun getChainResult(): Any?
    fun getMainTaskStatus(): ChainCallback.Status
    fun getChainStatus(): ChainCallback.Status
    fun setChainStatus(newStatus: ChainCallback.Status)
}