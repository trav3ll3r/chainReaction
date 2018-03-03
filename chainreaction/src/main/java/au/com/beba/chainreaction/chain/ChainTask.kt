package au.com.beba.chainreaction.chain

interface ChainTask {
    interface ChainTaskCallback {
        fun onResult(task: ChainTask, newMainTaskStatus: ChainCallback.Status, taskResult: Any?)
    }

    fun run(callback: ChainTaskCallback)
}