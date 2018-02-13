package au.com.beba.chainreaction.chain

interface Chain {
    val reactor: Reactor
    fun addToChain(vararg chainLinks: Chain): Chain

    fun startChain(callback: ChainCallback)
    fun startChainOnSameThread(callback: ChainCallback)

    // PHASES
    fun preMainTask()
    fun getChainTask(): ChainTask
    fun postMainTaskPhase(): () -> Any?
    fun linksPhase(): () -> Any?

    fun getChainLinks(): List<Chain>
    fun getChainResult(): Any?
    fun getChainStatus(): ChainCallback.Status
    fun setChainStatus(newStatus: ChainCallback.Status)
}