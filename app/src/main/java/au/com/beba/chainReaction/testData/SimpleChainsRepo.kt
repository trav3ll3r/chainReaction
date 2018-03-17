package au.com.beba.chainReaction.testData

import android.content.Intent
import au.com.beba.chainReaction.*
import au.com.beba.chainreaction.chain.BaseChain
import au.com.beba.chainreaction.chain.ChainCallback
import au.com.beba.chainreaction.chain.ChainTask
import au.com.beba.chainreaction.chain.Reactor
import au.com.beba.chainreaction.logger.ConsoleLogger
import au.com.beba.chainreaction.reactor.PassThroughReactor

abstract class AbcChain(override val reactor: Reactor)
    : ChainWithRequest(reactor) {
    protected open var status = ChainCallback.Status.SUCCESS
    protected open var taskResult = "?"
    var defaultSleep: Long = 1000
    var sleepMultiplier: Long = 1

    val chainId: String
        get() {
            return taskResult
        }

    override fun preMainTask() {
        super.preMainTask()
        broadcastChainChanged("preMainTask")

    }

    override fun getChainTask(): ChainTask {
        return object : ChainTask {
            override fun run(callback: ChainTask.ChainTaskCallback) {
                ConsoleLogger.log(TAG, "Running task with status=[%s] and result=[%s]".format(status, taskResult))
                Thread.sleep(getSleepTime())
                callback.onResult(this, status, taskResult)
            }
        }
    }

    override fun postMainTask() {
        super.postMainTask()
        broadcastChainChanged("postMainTask")
    }

    override fun chainFinishing() {
        super.chainFinishing()
        ConsoleLogger.log("FireBroadcast chainFinishing %s".format(this::class.java.simpleName))
        broadcastChainChanged("chainFinishing")
    }

    fun getSleepTime(): Long {
        return defaultSleep * sleepMultiplier
    }

    protected fun broadcastChainChanged(event: String) {
        val chainTag = this::class.java.simpleName
        ConsoleLogger.log(TAG, "Send broadcast with tag [%s]".format(chainTag))
        (reactor as ReactorWithBroadcast).localBroadcast?.sendBroadcast(
                Intent(CHAIN_REACTION_EVENT)
                        .putExtra(CHAIN_TAG, chainTag)
                        .putExtra(CHAIN_EVENT, event)
        )
    }
}

class AChain(override val reactor: Reactor = PassThroughReactor()) : AbcChain(reactor) {
    @Suppress("PropertyName")
    override val TAG: String = AChain::class.java.simpleName
    override var taskResult = "A"
}

class BChain(override val reactor: Reactor = PassThroughReactor()) : AbcChain(reactor) {
    @Suppress("PropertyName")
    override val TAG: String = BChain::class.java.simpleName
    override var taskResult = "B"
}

class B1Chain(override val reactor: Reactor = PassThroughReactor()) : AbcChain(reactor) {
    @Suppress("PropertyName")
    override val TAG: String = B1Chain::class.java.simpleName
    override var taskResult = "B1"
}

class B2Chain(override val reactor: Reactor = PassThroughReactor()) : AbcChain(reactor) {
    @Suppress("PropertyName")
    override val TAG: String = B2Chain::class.java.simpleName
    override var taskResult = "B2"
}

class B3Chain(override val reactor: Reactor = PassThroughReactor()) : AbcChain(reactor) {
    @Suppress("PropertyName")
    override val TAG: String = B3Chain::class.java.simpleName
    override var taskResult = "B3"
}

class CChain(override val reactor: Reactor = PassThroughReactor()) : AbcChain(reactor) {
    @Suppress("PropertyName")
    override val TAG: String = CChain::class.java.simpleName
    override var taskResult = "C"
}

class C1Chain(override val reactor: Reactor = PassThroughReactor()) : AbcChain(reactor) {
    @Suppress("PropertyName")
    override val TAG: String = C1Chain::class.java.simpleName
    override var taskResult = "C1"
}

class C2Chain(override val reactor: Reactor = PassThroughReactor()) : AbcChain(reactor) {
    @Suppress("PropertyName")
    override val TAG: String = C2Chain::class.java.simpleName
    override var taskResult = "C2"
}

class C3Chain(override val reactor: Reactor = PassThroughReactor()) : AbcChain(reactor) {
    @Suppress("PropertyName")
    override val TAG: String = C3Chain::class.java.simpleName
    override var taskResult = "C3"
}

class C4Chain(override val reactor: Reactor = PassThroughReactor()) : AbcChain(reactor) {
    @Suppress("PropertyName")
    override val TAG: String = C4Chain::class.java.simpleName
    override var taskResult = "C4"
}

class DChain(override val reactor: Reactor = PassThroughReactor()) : AbcChain(reactor) {
    @Suppress("PropertyName")
    override val TAG: String = DChain::class.java.simpleName
    override var taskResult = "D"
}

class EChain(override val reactor: Reactor = PassThroughReactor()) : AbcChain(reactor) {
    @Suppress("PropertyName")
    override val TAG: String = EChain::class.java.simpleName
    override var taskResult = "E"
}

class E1Chain(override val reactor: Reactor = PassThroughReactor()) : AbcChain(reactor) {
    @Suppress("PropertyName")
    override val TAG: String = E1Chain::class.java.simpleName
    override var taskResult = "E1"
}

class E2Chain(override val reactor: Reactor = PassThroughReactor()) : AbcChain(reactor) {
    @Suppress("PropertyName")
    override val TAG: String = E2Chain::class.java.simpleName
    override var taskResult = "E2"
}

class FChain(override val reactor: Reactor = PassThroughReactor()) : AbcChain(reactor) {
    @Suppress("PropertyName")
    override val TAG: String = FChain::class.java.simpleName
    override var taskResult = "F"
}

