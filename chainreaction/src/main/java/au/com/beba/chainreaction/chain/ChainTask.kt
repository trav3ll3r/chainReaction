package au.com.beba.chainreaction.chain

interface ChainTask {
    interface ChainTaskCallback {
        fun onResult(task: ChainTask, newStatus: ChainCallback.Status, taskResult: Any?)
    }

    fun run(callback: ChainTaskCallback)
}