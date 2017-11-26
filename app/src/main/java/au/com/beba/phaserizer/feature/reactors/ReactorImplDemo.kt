package au.com.beba.phaserizer.feature.reactors

class LoggingInReactor : BaseChainReaction() {
    companion object {
        private val TAG = LoggingInReactor::class.java.simpleName
    }

    override fun getLinkTask(): ChainTask {
        return object : ChainTask {
            override fun run(callback: RectorCallback) {
                val status = ChainTaskCallback.Status.SUCCESS
                println("%s: Running task with %s status".format(TAG, status))
                callback.onResult(this, status, "L")
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
                println("%s: Running task with %s status".format(TAG, status))
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
                println("%s: Running task with %s status".format(TAG, status))
                callback.onResult(this, status, "PS")
            }
        }
    }
}
