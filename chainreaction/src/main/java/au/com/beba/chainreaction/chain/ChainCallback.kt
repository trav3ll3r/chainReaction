package au.com.beba.chainreaction.chain

interface ChainCallback<in CHAIN> {
    fun onDone(completedChain: CHAIN)

    enum class Status {
        NONE,
        NOT_STARTED,
        QUEUED,
        IN_PROGRESS,
        ERROR,
        SUCCESS
    }
}