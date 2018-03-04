package au.com.beba.chainReaction.testData

import android.content.Intent
import au.com.beba.chainReaction.CHAIN_CLASS
import au.com.beba.chainReaction.CHAIN_EVENT
import au.com.beba.chainReaction.CHAIN_REACTION_EVENT
import au.com.beba.chainReaction.ReactorWithBroadcastIml
import au.com.beba.chainreaction.chain.BaseChain
import au.com.beba.chainreaction.chain.ChainCallback
import au.com.beba.chainreaction.chain.ChainTask
import au.com.beba.chainreaction.chain.Reactor
import au.com.beba.chainreaction.logger.ConsoleLogger
import au.com.beba.chainreaction.reactor.PassThroughReactor

abstract class AbcChain(override val reactor: Reactor)
    : BaseChain(reactor) {
    protected open val status = ChainCallback.Status.SUCCESS
    protected open val taskResult = "?"
    var defaultSleep: Long = 1000
    var sleepMultiplier: Long = 1

    val chainId: String
        get() {
//            return this::class.java.simpleName
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

    override fun runReactions() {
        reactions.forEach { it.task.invoke(this) }
    }

    override fun chainFinishing() {
        super.chainFinishing()
        ConsoleLogger.log("FireBroadcast chainFinishing %s".format(this::class.java.simpleName))
        broadcastChainChanged("chainFinishing")
    }

    fun getSleepTime(): Long {

        return defaultSleep * sleepMultiplier
    }

    private fun broadcastChainChanged(event: String) {
        val chainTag = this::class.java.simpleName
        ConsoleLogger.log(TAG, "Send broadcast with tag [%s]".format(chainTag))
        (reactor as ReactorWithBroadcastIml).localBroadcast.sendBroadcast(Intent(CHAIN_REACTION_EVENT).putExtra(CHAIN_CLASS, chainTag).putExtra(CHAIN_EVENT, event))
    }
}

class AChain(override val reactor: Reactor = PassThroughReactor()) : AbcChain(reactor) {
    @Suppress("PropertyName")
    override val TAG: String = AChain::class.java.simpleName
    override val taskResult = "A"
}

class BChain(override val reactor: Reactor = PassThroughReactor()) : AbcChain(reactor) {
    @Suppress("PropertyName")
    override val TAG: String = BChain::class.java.simpleName
    override val taskResult = "B"
}

class B1Chain(override val reactor: Reactor = PassThroughReactor()) : AbcChain(reactor) {
    @Suppress("PropertyName")
    override val TAG: String = B1Chain::class.java.simpleName
    override val taskResult = "B1"
}

class CChain(override val reactor: Reactor = PassThroughReactor()) : AbcChain(reactor) {
    @Suppress("PropertyName")
    override val TAG: String = CChain::class.java.simpleName
    override val taskResult = "C"
}

class C1Chain(override val reactor: Reactor = PassThroughReactor()) : AbcChain(reactor) {
    @Suppress("PropertyName")
    override val TAG: String = C1Chain::class.java.simpleName
    override val taskResult = "C1"
}

class C2Chain(override val reactor: Reactor = PassThroughReactor()) : AbcChain(reactor) {
    @Suppress("PropertyName")
    override val TAG: String = C2Chain::class.java.simpleName
    override val taskResult = "C2"
}

class DChain(override val reactor: Reactor = PassThroughReactor()) : AbcChain(reactor) {
    @Suppress("PropertyName")
    override val TAG: String = DChain::class.java.simpleName
    override val taskResult = "D"
}

class EChain(override val reactor: Reactor = PassThroughReactor()) : AbcChain(reactor) {
    @Suppress("PropertyName")
    override val TAG: String = EChain::class.java.simpleName
    override val taskResult = "E"
}

class E1Chain(override val reactor: Reactor = PassThroughReactor()) : AbcChain(reactor) {
    @Suppress("PropertyName")
    override val TAG: String = E1Chain::class.java.simpleName
    override val taskResult = "E1"
}

class FChain(override val reactor: Reactor = PassThroughReactor()) : AbcChain(reactor) {
    @Suppress("PropertyName")
    override val TAG: String = FChain::class.java.simpleName
    override val taskResult = "F"
}

