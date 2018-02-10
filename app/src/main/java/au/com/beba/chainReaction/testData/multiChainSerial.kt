package au.com.beba.chainReaction.testData

import au.com.beba.chainreaction.chain.ChainCallback
import au.com.beba.chainreaction.chain.ChainTask
import au.com.beba.chainreaction.logger.ConsoleLogger
import au.com.beba.chainreaction.reactor.BaseChainWithPhases


class AChain : BaseChainWithPhases() {
    @Suppress("PropertyName")
    override val TAG: String = AChain::class.java.simpleName

    override fun getChainTask(): ChainTask {
        return object : ChainTask {
            override fun run(callback: ChainTask.ChainTaskCallback) {
                val status = ChainCallback.Status.SUCCESS
                val taskResult = "A"
                ConsoleLogger.log(TAG, "Running task with status=[%s] and result=[%s]".format(status, taskResult))
                Thread.sleep(800)
                callback.onResult(this, status, taskResult)
            }
        }
    }
}

class BChain : BaseChainWithPhases() {
    @Suppress("PropertyName")
    override val TAG: String = BChain::class.java.simpleName

    override fun getChainTask(): ChainTask {
        return object : ChainTask {
            override fun run(callback: ChainTask.ChainTaskCallback) {
                val status = ChainCallback.Status.SUCCESS
                val taskResult = "B"
                ConsoleLogger.log(TAG, "Running task with status=[%s] and result=[%s]".format(status, taskResult))
                Thread.sleep(1000)
                callback.onResult(this, status, taskResult)
            }
        }
    }
}

class CChain : BaseChainWithPhases() {
    @Suppress("PropertyName")
    override val TAG: String = CChain::class.java.simpleName

    override fun getChainTask(): ChainTask {
        return object : ChainTask {
            override fun run(callback: ChainTask.ChainTaskCallback) {
                val status = ChainCallback.Status.SUCCESS
                val taskResult = "C"
                ConsoleLogger.log(TAG, "Running task with status=[%s] and result=[%s]".format(status, taskResult))
                Thread.sleep(1500)
                callback.onResult(this, status, taskResult)
            }
        }
    }
}

class C1Chain : BaseChainWithPhases() {
    @Suppress("PropertyName")
    override val TAG: String = C1Chain::class.java.simpleName

    override fun getChainTask(): ChainTask {
        return object : ChainTask {
            override fun run(callback: ChainTask.ChainTaskCallback) {
                val status = ChainCallback.Status.SUCCESS
                val taskResult = "C1"
                ConsoleLogger.log(TAG, "Running task with status=[%s] and result=[%s]".format(status, taskResult))
                Thread.sleep(300)
                callback.onResult(this, status, taskResult)
            }
        }
    }
}

class C2Chain : BaseChainWithPhases() {
    @Suppress("PropertyName")
    override val TAG: String = C2Chain::class.java.simpleName

    override fun getChainTask(): ChainTask {
        return object : ChainTask {
            override fun run(callback: ChainTask.ChainTaskCallback) {
                val status = ChainCallback.Status.SUCCESS
                val taskResult = "C2"
                ConsoleLogger.log(TAG, "Running task with status=[%s] and result=[%s]".format(status, taskResult))
                Thread.sleep(400)
                callback.onResult(this, status, taskResult)
            }
        }
    }
}
