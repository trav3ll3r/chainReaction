package au.com.beba.phaserizer.feature.phz

import java.util.concurrent.Callable
import java.util.concurrent.Executor

interface PhaseConductor {
    fun defaultErrorPhase(errorPhase: Phase): PhaseConductor
    fun withPhase(newPhase: Phase): PhaseConductor
    fun withPhases(vararg phases: Phase): PhaseConductor
    fun run()

    fun onAdvance()
    fun onError(failedPhase: Phase)
}

interface Phase {
    var name: String
    fun setConductor(conductor: PhaseConductor): Phase
    var errorPhase: Phase?
    fun include(newTask: Task): Phase
    fun errorPhaseCondition(policy: ErrorPolicy = ErrorPolicy.FIRST): Phase
    fun advancePhaseCondition(policy: SuccessPolicy = SuccessPolicy.ALL): Phase
    fun concurrencyExecutor(executor: Executor): Phase
    fun execute()
}

interface Task {
    var name: String
    fun getExecutableTask(): Callable<Task?>?
    fun setExecutableTask(executableTask: Callable<Task?>?): Task
    fun getExecuteResult(): Any?
    fun setExecuteResult(result: Any?)
    fun setErrorRule(errorRule: (Task) -> (Unit)): Task

    // STATUS CHANGES
    fun onQueued()
    fun onSuccess()
    fun onError()
    fun getStatus(): TaskStatus
}

enum class ErrorPolicy {
    FIRST,
    LAST
}

enum class SuccessPolicy {
    FIRST,
    LAST,
    ALL
}

enum class TaskStatus {
    NOT_EXECUTED,
    QUEUED,
    DONE_WITH_SUCCESS,
    DONE_WITH_ERROR
}