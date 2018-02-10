package au.com.beba.chainreaction.chain

interface Chain {
    fun addToChain(link: Chain)

    fun startChain(callback: ChainCallback)

    // PHASES
    fun preMainTaskPhase(): () -> Any?
    fun mainTaskPhase(): () -> Any?
    fun postMainTaskPhase(): () -> Any?
    fun getChainTask(): ChainTask
    fun linksPhase(): () -> Any?

    fun getChainResult(): Any?
    fun getChainStatus(): ChainCallback.Status
    fun setChainStatus(newStatus: ChainCallback.Status)
}