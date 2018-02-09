package au.com.beba.chainReaction.feature.chain

interface ChainCallback {
    fun onDone(status: Status)

    enum class Status {
        NOT_STARTED,
        IN_PROGRESS,
        ERROR,
        SUCCESS
    }
}