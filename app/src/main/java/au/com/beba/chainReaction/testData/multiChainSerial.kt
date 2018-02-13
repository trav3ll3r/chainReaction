package au.com.beba.chainReaction.testData

import android.content.Context
import android.content.Intent
import au.com.beba.chainReaction.CHAIN_CLASS
import au.com.beba.chainReaction.CHAIN_REACTION_EVENT
import au.com.beba.chainReaction.ReactorWithBroadcastIml
import au.com.beba.chainreaction.chain.BaseChain
import au.com.beba.chainreaction.chain.ChainCallback
import au.com.beba.chainreaction.chain.ChainTask
import au.com.beba.chainreaction.logger.ConsoleLogger

abstract class AbcChain(context: Context)
    : BaseChain(reactor = ReactorWithBroadcastIml(context)) {
    protected open val status = ChainCallback.Status.SUCCESS
    protected open val taskResult = "?"

    override fun preMainTask() {
        super.preMainTask()
        broadcastChainChanged()
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
        broadcastChainChanged()
    }

    override fun runReactions() {
        reactions.forEach { it.task.invoke(this) }
    }

    override fun chainFinished() {
        super.chainFinished()
        broadcastChainChanged()
    }

    fun getSleepTime(): Long {
        val defaultSleep: Long = 200
        val sleepMultiplier: Long = 2
        return defaultSleep * sleepMultiplier
    }

    private fun broadcastChainChanged() {
        val chainTag = this::class.java.simpleName
        ConsoleLogger.log(TAG, "Send broadcast with tag [%s]".format(chainTag))
        (reactor as ReactorWithBroadcastIml).localBroadcast.sendBroadcast(Intent(CHAIN_REACTION_EVENT).putExtra(CHAIN_CLASS, chainTag))
    }
}

class AChain(context: Context) : AbcChain(context) {
    @Suppress("PropertyName")
    override val TAG: String = AChain::class.java.simpleName
    override val taskResult = "A"
}

class BChain(context: Context) : AbcChain(context) {
    @Suppress("PropertyName")
    override val TAG: String = BChain::class.java.simpleName
    override val taskResult = "B"
}

class CChain(context: Context) : AbcChain(context) {
    @Suppress("PropertyName")
    override val TAG: String = CChain::class.java.simpleName
    override val taskResult = "C"
}

class C1Chain(context: Context) : AbcChain(context) {
    @Suppress("PropertyName")
    override val TAG: String = C1Chain::class.java.simpleName
    override val taskResult = "C1"
}

class C2Chain(context: Context) : AbcChain(context) {
    @Suppress("PropertyName")
    override val TAG: String = C2Chain::class.java.simpleName
    override val taskResult = "C2"
}

class DChain(context: Context) : AbcChain(context) {
    @Suppress("PropertyName")
    override val TAG: String = DChain::class.java.simpleName
    override val taskResult = "D"
}

class EChain(context: Context) : AbcChain(context) {
    @Suppress("PropertyName")
    override val TAG: String = EChain::class.java.simpleName
    override val taskResult = "E"
}

class E1Chain(context: Context) : AbcChain(context) {
    @Suppress("PropertyName")
    override val TAG: String = E1Chain::class.java.simpleName
    override val taskResult = "E1"
}

class FChain(context: Context) : AbcChain(context) {
    @Suppress("PropertyName")
    override val TAG: String = FChain::class.java.simpleName
    override val taskResult = "F1"
}

