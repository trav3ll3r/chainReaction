package au.com.beba.chainReaction

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

class C1ChainSuccess : BaseChain() {
    @Suppress("PropertyName")
    override val TAG: String = C1ChainSuccess::class.java.simpleName

    override fun getChainTask(): ChainTask {
        return object : ChainTask {
            override fun run(callback: ChainTask.ChainTaskCallback) {
                val status = ChainCallback.Status.SUCCESS
                val taskResult = "C1"
                ConsoleLogger.log(TAG, "Running task with status=[%s] and result=[%s]".format(status, taskResult))
                Thread.sleep(500)
                callback.onResult(this, status, taskResult)
            }
        }
    }
}

class C11ChainSuccess : BaseChain() {
    @Suppress("PropertyName")
    override val TAG: String = C11ChainSuccess::class.java.simpleName

    override fun getChainTask(): ChainTask {
        return object : ChainTask {
            override fun run(callback: ChainTask.ChainTaskCallback) {
                val status = ChainCallback.Status.SUCCESS
                val taskResult = "C11"
                ConsoleLogger.log(TAG, "Running task with status=[%s] and result=[%s]".format(status, taskResult))
                Thread.sleep(500)
                callback.onResult(this, status, taskResult)
            }
        }
    }
}

class C12ChainSuccess : BaseChain() {
    @Suppress("PropertyName")
    override val TAG: String = C12ChainSuccess::class.java.simpleName

    override fun getChainTask(): ChainTask {
        return object : ChainTask {
            override fun run(callback: ChainTask.ChainTaskCallback) {
                val status = ChainCallback.Status.SUCCESS
                val taskResult = "C12"
                ConsoleLogger.log(TAG, "Running task with status=[%s] and result=[%s]".format(status, taskResult))
                Thread.sleep(500)
                callback.onResult(this, status, taskResult)
            }
        }
    }
}

class C121ChainSuccess : BaseChain() {
    @Suppress("PropertyName")
    override val TAG: String = C121ChainSuccess::class.java.simpleName

    override fun getChainTask(): ChainTask {
        return object : ChainTask {
            override fun run(callback: ChainTask.ChainTaskCallback) {
                val status = ChainCallback.Status.SUCCESS
                val taskResult = "C121"
                ConsoleLogger.log(TAG, "Running task with status=[%s] and result=[%s]".format(status, taskResult))
                Thread.sleep(500)
                callback.onResult(this, status, taskResult)
            }
        }
    }
}

class D1ChainSuccess : BaseChain() {
    @Suppress("PropertyName")
    override val TAG: String = D1ChainSuccess::class.java.simpleName

    override fun getChainTask(): ChainTask {
        return object : ChainTask {
            override fun run(callback: ChainTask.ChainTaskCallback) {
                val status = ChainCallback.Status.SUCCESS
                val taskResult = "D1"
                ConsoleLogger.log(TAG, "Running task with status=[%s] and result=[%s]".format(status, taskResult))
                Thread.sleep(500)
                callback.onResult(this, status, taskResult)
            }
        }
    }
}

class E1ChainSuccess : BaseChain() {
    @Suppress("PropertyName")
    override val TAG: String = E1ChainSuccess::class.java.simpleName

    override fun getChainTask(): ChainTask {
        return object : ChainTask {
            override fun run(callback: ChainTask.ChainTaskCallback) {
                val status = ChainCallback.Status.SUCCESS
                val taskResult = "E1"
                ConsoleLogger.log(TAG, "Running task with status=[%s] and result=[%s]".format(status, taskResult))
                Thread.sleep(500)
                callback.onResult(this, status, taskResult)
            }
        }
    }
}

class E11ChainSuccess : BaseChain() {
    @Suppress("PropertyName")
    override val TAG: String = E11ChainSuccess::class.java.simpleName

    override fun getChainTask(): ChainTask {
        return object : ChainTask {
            override fun run(callback: ChainTask.ChainTaskCallback) {
                val status = ChainCallback.Status.SUCCESS
                val taskResult = "E11"
                ConsoleLogger.log(TAG, "Running task with status=[%s] and result=[%s]".format(status, taskResult))
                Thread.sleep(500)
                callback.onResult(this, status, taskResult)
            }
        }
    }
}

class F1ChainSuccess : BaseChain() {
    @Suppress("PropertyName")
    override val TAG: String = F1ChainSuccess::class.java.simpleName

    override fun getChainTask(): ChainTask {
        return object : ChainTask {
            override fun run(callback: ChainTask.ChainTaskCallback) {
                val status = ChainCallback.Status.SUCCESS
                val taskResult = "F1"
                ConsoleLogger.log(TAG, "Running task with status=[%s] and result=[%s]".format(status, taskResult))
                Thread.sleep(500)
                callback.onResult(this, status, taskResult)
            }
        }
    }
}