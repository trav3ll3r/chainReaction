package au.com.beba.phaserizer.feature.reactors

class LoggingInReactor : BaseChainReaction() {
    companion object {
        private val TAG = LoggingInReactor::class.java.simpleName
    }

    override fun getLinkTask(): ChainTask {
        return object : ChainTask {
            override fun run(callback: RectorCallback) {
                val status = ChainTaskCallback.Status.SUCCESS
                val taskResult = "L"
                println("%s: Running task with status=[%s] and result=[%s]".format(TAG, status, taskResult))
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
            override fun run(callback: RectorCallback) {
                val status = ChainTaskCallback.Status.SUCCESS
                val taskResult = "S"
                println("%s: Running task with status=[%s] and result=[%s]".format(TAG, status, taskResult))
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
            override fun run(callback: RectorCallback) {
                val status = ChainTaskCallback.Status.SUCCESS
                val taskResult = "PS"
                println("%s: Running task with status=[%s] and result=[%s]".format(TAG, status, taskResult))
                callback.onResult(this, status, "PS")
            }
        }
    }
}
