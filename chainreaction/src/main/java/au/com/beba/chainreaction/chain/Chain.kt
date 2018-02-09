package au.com.beba.chainreaction.chain

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