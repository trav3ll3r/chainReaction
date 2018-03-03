package au.com.beba.chainreaction.testData

import au.com.beba.chainreaction.chain.BaseChain
import au.com.beba.chainreaction.chain.ChainCallback
import au.com.beba.chainreaction.chain.ChainTask
import au.com.beba.chainreaction.logger.ConsoleLogger

abstract class BaseTestChain : BaseChain() {
    @Suppress("PropertyName")
    override val TAG: String = A1ChainSuccess::class.java.simpleName
    open val status = ChainCallback.Status.SUCCESS
    open val taskResult = "A1"
    open val duration = 500L

    override fun getChainTask(): ChainTask {
        return object : ChainTask {
            override fun run(callback: ChainTask.ChainTaskCallback) {
                ConsoleLogger.log(TAG, "Running task with status=[%s] and result=[%s]".format(status, taskResult))
                Thread.sleep(duration)
                callback.onResult(this, status, taskResult)
            }
        }
    }
}

class A1ChainSuccess : BaseTestChain() {
    @Suppress("PropertyName")
    override val TAG: String = A1ChainSuccess::class.java.simpleName
    override val taskResult = "A1"
}

class A2ChainError : BaseTestChain() {
    @Suppress("PropertyName")
    override val TAG: String = A2ChainError::class.java.simpleName
    override val status = ChainCallback.Status.ERROR
    override val taskResult = "A2"
    override val duration = 200L
}

class B1ChainSuccess : BaseTestChain() {
    @Suppress("PropertyName")
    override val TAG: String = B1ChainSuccess::class.java.simpleName
    override val taskResult = "B1"
}

class C1ChainSuccess : BaseTestChain() {
    @Suppress("PropertyName")
    override val TAG: String = C1ChainSuccess::class.java.simpleName
    override val taskResult = "C1"
}

class C11ChainSuccess : BaseTestChain() {
    @Suppress("PropertyName")
    override val TAG: String = C11ChainSuccess::class.java.simpleName
    override val taskResult = "C11"
}

class C12ChainSuccess : BaseTestChain() {
    @Suppress("PropertyName")
    override val TAG: String = C12ChainSuccess::class.java.simpleName
    override val taskResult = "C12"
}

class D1ChainSuccess : BaseTestChain() {
    @Suppress("PropertyName")
    override val TAG: String = D1ChainSuccess::class.java.simpleName
    override val taskResult = "D1"
}

class E1ChainSuccess : BaseTestChain() {
    @Suppress("PropertyName")
    override val TAG: String = E1ChainSuccess::class.java.simpleName
    override val taskResult = "E1"
}

class E2ChainSuccess : BaseTestChain() {
    @Suppress("PropertyName")
    override val TAG: String = E2ChainSuccess::class.java.simpleName
    override val taskResult = "E2"
}

class F1ChainSuccess : BaseTestChain() {
    @Suppress("PropertyName")
    override val TAG: String = F1ChainSuccess::class.java.simpleName
    override val taskResult = "F1"
}