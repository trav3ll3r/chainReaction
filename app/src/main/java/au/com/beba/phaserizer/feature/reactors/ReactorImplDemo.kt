package au.com.beba.phaserizer.feature.reactors

import au.com.beba.phaserizer.feature.ConsoleLogger


class LoggingInReactor : BaseChainReaction() {
    companion object {
        private val TAG = LoggingInReactor::class.java.simpleName
    }

    override fun getLinkTask(): ChainTask {
        return object : ChainTask {
            override fun run(callback: ReactorCallback) {
                val status = ChainReactionCallback.Status.SUCCESS
                val taskResult = "L"
                ConsoleLogger.log("%s: Running task with status=[%s] and result=[%s]".format(TAG, status, taskResult))
                callback.onResult(this, status, taskResult)
            }
        }
    }
}

class SignInReactor : BaseChainReaction() {
    companion object {
        private val TAG = SignInReactor::class.java.simpleName
    }

    override fun getLinkTask(): ChainTask {
        return object : ChainTask {
            override fun run(callback: ReactorCallback) {
                val status = ChainReactionCallback.Status.SUCCESS
                val taskResult = "S"
                ConsoleLogger.log("%s: Running task with status=[%s] and result=[%s]".format(TAG, status, taskResult))
                Thread.sleep(1000)
                callback.onResult(this, status, "S")
            }
        }
    }
}

class PostSignInReactor : BaseChainReaction() {
    companion object {
        private val TAG = PostSignInReactor::class.java.simpleName
    }

    override fun getLinkTask(): ChainTask {
        return object : ChainTask {
            override fun run(callback: ReactorCallback) {
                val status = ChainReactionCallback.Status.SUCCESS
                val taskResult = "PS"
                ConsoleLogger.log("%s: Running task with status=[%s] and result=[%s]".format(TAG, status, taskResult))
                Thread.sleep(1500)
                callback.onResult(this, status, "PS")
            }
        }
    }
}