package au.com.beba.phaserizer.feature.reactors

import au.com.beba.phaserizer.feature.ConsoleLogger

class LoggingInChain : BaseChain() {
    override val TAG = LoggingInChain::class.java.simpleName

    override fun getChainTask(): ChainTask {
        return object : ChainTask {
            override fun run(callback: ChainTask.ReactorTaskCallback) {
                val status = ChainCallback.Status.SUCCESS
                val taskResult = "L"
                ConsoleLogger.log(TAG, "Running task with status=[%s] and result=[%s]".format(status, taskResult))
                callback.onResult(this, status, taskResult)
            }
        }
    }
}

class SignInChain : BaseChain() {
    override val TAG = SignInChain::class.java.simpleName

    override fun getChainTask(): ChainTask {
        return object : ChainTask {
            override fun run(callback: ChainTask.ReactorTaskCallback) {
                val status = ChainCallback.Status.SUCCESS
                val taskResult = "S"
                ConsoleLogger.log(TAG, "Running task with status=[%s] and result=[%s]".format(status, taskResult))
                Thread.sleep(1000)
                callback.onResult(this, status, taskResult)
            }
        }
    }
}

class GetAccountsChain : BaseChain() {
    override val TAG = GetAccountsChain::class.java.simpleName

    override fun getChainTask(): ChainTask {
        return object : ChainTask {
            override fun run(callback: ChainTask.ReactorTaskCallback) {
                val status = ChainCallback.Status.SUCCESS
                val taskResult = "S-GA"
                ConsoleLogger.log(TAG, "Running task with status=[%s] and result=[%s]".format(status, taskResult))
                Thread.sleep(300)
                callback.onResult(this, status, taskResult)
            }
        }
    }
}

class GetCardsChain : BaseChain() {
    override val TAG = GetCardsChain::class.java.simpleName

    override fun getChainTask(): ChainTask {
        return object : ChainTask {
            override fun run(callback: ChainTask.ReactorTaskCallback) {
                val status = ChainCallback.Status.SUCCESS
                val taskResult = "S-GC"
                ConsoleLogger.log(TAG, "Running task with status=[%s] and result=[%s]".format(status, taskResult))
                Thread.sleep(400)
                callback.onResult(this, status, taskResult)
            }
        }
    }
}

class PostSignInChain : BaseChain() {
    override val TAG = PostSignInChain::class.java.simpleName

    override fun getChainTask(): ChainTask {
        return object : ChainTask {
            override fun run(callback: ChainTask.ReactorTaskCallback) {
                val status = ChainCallback.Status.SUCCESS
                val taskResult = "PS"
                ConsoleLogger.log(TAG, "Running task with status=[%s] and result=[%s]".format(status, taskResult))
                Thread.sleep(1500)
                callback.onResult(this, status, taskResult)
            }
        }
    }
}