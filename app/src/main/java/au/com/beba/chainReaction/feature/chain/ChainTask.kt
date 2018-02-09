package au.com.beba.chainReaction.feature.chain

interface ChainTask {
    interface ChainTaskCallback {
        fun onResult(task: ChainTask, newStatus: ChainCallback.Status, taskResult: Any?)
    }

    fun run(callback: ChainTaskCallback)
}