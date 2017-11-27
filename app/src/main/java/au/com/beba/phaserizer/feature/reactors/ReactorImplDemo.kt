package au.com.beba.phaserizer.feature.reactors

import au.com.beba.phaserizer.feature.ConsoleLogger

class LoggingInReaction : BaseChainReaction() {
    companion object {
        private val TAG = LoggingInReaction::class.java.simpleName
    }

    override fun getLinkTask(): ChainTask {
        return object : ChainTask {
            override fun run(callback: ChainTask.ReactorTaskCallback) {
                val status = ChainReactionCallback.Status.SUCCESS
                val taskResult = "L"
                ConsoleLogger.log("%s: Running task with status=[%s] and result=[%s]".format(TAG, status, taskResult))
                callback.onResult(this, status, taskResult)
            }
        }
    }
}

class SignInReaction : BaseChainReaction() {
    companion object {
        private val TAG = SignInReaction::class.java.simpleName
    }

    override fun getLinkTask(): ChainTask {
        return object : ChainTask {
            override fun run(callback: ChainTask.ReactorTaskCallback) {
                val status = ChainReactionCallback.Status.SUCCESS
                val taskResult = "S"
                ConsoleLogger.log("%s: Running task with status=[%s] and result=[%s]".format(TAG, status, taskResult))
                Thread.sleep(1000)
                callback.onResult(this, status, "S")
            }
        }
    }
}

class PostSignInReaction : BaseChainReaction() {
    companion object {
        private val TAG = PostSignInReaction::class.java.simpleName
    }

    override fun getLinkTask(): ChainTask {
        return object : ChainTask {
            override fun run(callback: ChainTask.ReactorTaskCallback) {
                val status = ChainReactionCallback.Status.SUCCESS
                val taskResult = "PS"
                ConsoleLogger.log("%s: Running task with status=[%s] and result=[%s]".format(TAG, status, taskResult))
                Thread.sleep(1500)
                callback.onResult(this, status, "PS")
            }
        }
    }
}