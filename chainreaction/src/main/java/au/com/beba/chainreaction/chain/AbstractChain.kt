package au.com.beba.chainreaction.chain

import au.com.beba.chainreaction.logger.ConsoleLogger
import au.com.beba.chainreaction.reactor.PassThroughReactor

abstract class AbstractChain(override val reactor: Reactor = PassThroughReactor()) : Chain {
    @Suppress("PropertyName")
    protected open val TAG: String = AbstractChain::class.java.simpleName

    private var result: Any? = null
    private var chainStatus = ChainCallback.Status.NOT_STARTED
    protected var chainMainTaskStatus = ChainCallback.Status.NONE

    override fun getChainResult(): Any? {
        return result
    }

    protected fun setChainResult(value: Any?) {
        ConsoleLogger.log(TAG, "setChainResult | taskResult=%s".format(value))
        result = value
    }

    override fun getMainTaskStatus(): ChainCallback.Status {
        return chainMainTaskStatus
    }

    override fun getChainStatus(): ChainCallback.Status {
        return this.chainStatus
    }

    override fun setChainStatus(newStatus: ChainCallback.Status) {
        ConsoleLogger.log(TAG, "setChainStatus | status=%s => %s".format(this.chainStatus, newStatus))
        this.chainStatus = newStatus
    }
}