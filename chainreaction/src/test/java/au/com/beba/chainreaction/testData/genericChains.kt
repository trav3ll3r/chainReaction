package au.com.beba.chainreaction.testData

import au.com.beba.chainreaction.chain.BaseChain
import au.com.beba.chainreaction.chain.ChainCallback
import au.com.beba.chainreaction.chain.ChainTask
import au.com.beba.chainreaction.logger.ConsoleLogger

class A1ChainSuccess : BaseChain() {
    @Suppress("PropertyName")
    override val TAG: String = A1ChainSuccess::class.java.simpleName

    override fun getChainTask(): ChainTask {
        return object : ChainTask {
            override fun run(callback: ChainTask.ChainTaskCallback) {
                val status = ChainCallback.Status.SUCCESS
                val taskResult = "A1"
                ConsoleLogger.log(TAG, "Running task with status=[%s] and result=[%s]".format(status, taskResult))
                Thread.sleep(500)
                callback.onResult(this, status, taskResult)
            }
        }
    }
}

class A2ChainError : BaseChain() {
    @Suppress("PropertyName")
    override val TAG: String = A2ChainError::class.java.simpleName

    override fun getChainTask(): ChainTask {
        return object : ChainTask {
            override fun run(callback: ChainTask.ChainTaskCallback) {
                val status = ChainCallback.Status.ERROR
                val taskResult = "A2"
                ConsoleLogger.log(TAG, "Running task with status=[%s] and result=[%s]".format(status, taskResult))
                Thread.sleep(200)
                callback.onResult(this, status, taskResult)
            }
        }
    }
}

class B1ChainSuccess : BaseChain() {
    @Suppress("PropertyName")
    override val TAG: String = B1ChainSuccess::class.java.simpleName

    override fun getChainTask(): ChainTask {
        return object : ChainTask {
            override fun run(callback: ChainTask.ChainTaskCallback) {
                val status = ChainCallback.Status.SUCCESS
                val taskResult = "B1"
                ConsoleLogger.log(TAG, "Running task with status=[%s] and result=[%s]".format(status, taskResult))
                Thread.sleep(500)
                callback.onResult(this, status, taskResult)
            }
        }
    }
}