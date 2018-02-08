package au.com.beba.phaserizer.feature.reactors

import au.com.beba.phaserizer.feature.ConsoleLogger

abstract class AbstractChain(private val reactor: Reactor = IdleReactor()) : Chain {
    @Suppress("PropertyName")
    protected open val TAG: String = AbstractChain::class.java.simpleName

    private var result: Any? = null
    private var status = ChainCallback.Status.NOT_STARTED

    override fun getChainResult(): Any? {
        //ConsoleLogger.log(TAG, "{%s} getChainResult | taskResult=%s".format("CHAIN", result))
        return result
    }

    protected fun setChainResult(value: Any?) {
        ConsoleLogger.log(TAG, "setChainResult | taskResult=%s".format(value))
        result = value
    }

    override fun getChainStatus(): ChainCallback.Status {
        //ConsoleLogger.log(TAG, "getChainStatus | status=%s".format(status))
        return this.status
    }

    override fun setChainStatus(newStatus: ChainCallback.Status) {
        ConsoleLogger.log(TAG, "setChainStatus | status=%s => %s".format(this.status, newStatus))
        this.status = newStatus
    }
}