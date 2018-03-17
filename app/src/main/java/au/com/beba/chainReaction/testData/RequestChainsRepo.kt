package au.com.beba.chainReaction.testData

import au.com.beba.chainreaction.chain.ChainCallback
import au.com.beba.chainreaction.chain.ChainTask
import au.com.beba.chainreaction.chain.Reactor
import au.com.beba.chainreaction.logger.ConsoleLogger

enum class ChainRequestType {
    LETTER,
    NUMBER
}

class AnyLetterChain(override val reactor: Reactor)
    : AbcChain(reactor) {

    @Suppress("PropertyName")
    override val TAG: String = AnyLetterChain::class.java.simpleName
    override var taskResult = "[A-Z]A"

    override fun preMainTask() {
        resetRequests()
        requestExternalValue(ChainRequestType.LETTER)
        super.preMainTask()
    }

    override fun getChainTask(): ChainTask {
        return object : ChainTask {
            override fun run(callback: ChainTask.ChainTaskCallback) {
                taskResult = requestExternalValue(ChainRequestType.LETTER)
                if (taskResult.equals("A", true)) {
                    status = ChainCallback.Status.SUCCESS
                } else {
                    status = ChainCallback.Status.ERROR
                }

                ConsoleLogger.log(TAG, "Running task with status=[%s] and result=[%s]".format(status, taskResult))
                Thread.sleep(getSleepTime())
                callback.onResult(this, status, taskResult)
            }
        }
    }
}

class AnyNumberChain(override val reactor: Reactor) : AbcChain(reactor) {
    @Suppress("PropertyName")
    override val TAG: String = AnyNumberChain::class.java.simpleName
    override var taskResult = "[0-9]5"

    override fun preMainTask() {
        resetRequests()
        requestExternalValue(ChainRequestType.NUMBER)
        super.preMainTask()
    }

    override fun getChainTask(): ChainTask {
        return object : ChainTask {
            override fun run(callback: ChainTask.ChainTaskCallback) {
                taskResult = requestExternalValue(ChainRequestType.NUMBER)
                if (taskResult.equals("5", true)) {
                    status = ChainCallback.Status.SUCCESS
                } else {
                    status = ChainCallback.Status.ERROR
                }

                ConsoleLogger.log(TAG, "Running task with status=[%s] and result=[%s]".format(status, taskResult))
                Thread.sleep(getSleepTime())
                callback.onResult(this, status, taskResult)
            }
        }
    }
}
