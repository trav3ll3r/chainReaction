package au.com.beba.chainReaction.testData

import au.com.beba.chainreaction.chain.BaseChain
import au.com.beba.chainreaction.chain.ChainCallback
import au.com.beba.chainreaction.chain.ChainTask
import au.com.beba.chainreaction.logger.ConsoleLogger

abstract class AbcChain : BaseChain() {
    protected open val status = ChainCallback.Status.SUCCESS
    protected open val taskResult = "?"

    override fun getChainTask(): ChainTask {
        return object : ChainTask {
            override fun run(callback: ChainTask.ChainTaskCallback) {
                ConsoleLogger.log(TAG, "Running task with status=[%s] and result=[%s]".format(status, taskResult))
                Thread.sleep(getSleepTime())
                callback.onResult(this, status, taskResult)
            }
        }
    }

    fun getSleepTime(): Long {
        val defaultSleep: Long = 200
        val sleepMultiplier: Long = 2
        return defaultSleep * sleepMultiplier
    }
}

class AChain : AbcChain() {
    @Suppress("PropertyName")
    override val TAG: String = AChain::class.java.simpleName
    override val taskResult = "A"
}

class BChain : AbcChain() {
    @Suppress("PropertyName")
    override val TAG: String = BChain::class.java.simpleName
    override val taskResult = "B"
}

class CChain : AbcChain() {
    @Suppress("PropertyName")
    override val TAG: String = CChain::class.java.simpleName
    override val taskResult = "C"
}

class C1Chain : AbcChain() {
    @Suppress("PropertyName")
    override val TAG: String = C1Chain::class.java.simpleName
    override val taskResult = "C1"
}

class C2Chain : AbcChain() {
    @Suppress("PropertyName")
    override val TAG: String = C2Chain::class.java.simpleName
    override val taskResult = "C2"
}

class DChain : AbcChain() {
    @Suppress("PropertyName")
    override val TAG: String = DChain::class.java.simpleName
    override val taskResult = "D"
}

class EChain : AbcChain() {
    @Suppress("PropertyName")
    override val TAG: String = EChain::class.java.simpleName
    override val taskResult = "E"
}

class E1Chain : AbcChain() {
    @Suppress("PropertyName")
    override val TAG: String = E1Chain::class.java.simpleName
    override val taskResult = "E1"
}

class FChain : AbcChain() {
    @Suppress("PropertyName")
    override val TAG: String = FChain::class.java.simpleName
    override val taskResult = "F1"
}

