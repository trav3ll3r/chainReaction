package au.com.beba.chainreaction.chain

interface ChainCallback {
    fun onDone(status: Status)

    enum class Status {
        NONE,
        NOT_STARTED,
        QUEUED,
        IN_PROGRESS,
        ERROR,
        SUCCESS
    }
}