package au.com.beba.phaserizer.feature.phz

import android.util.Log
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Callable
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class BasePhaseConductor : PhaseConductor {
    private val TAG = "PhaseFlow.Conductor"
    private var phases = mutableListOf<Phase>()
    private var phaseIndex = 0
    private var defaultErrorPhase: Phase = LogStatePhase()
    override fun defaultErrorPhase(errorPhase: Phase): PhaseConductor {
        defaultErrorPhase = errorPhase
        return this
    }

    override fun withPhase(newPhase: Phase): PhaseConductor {
        phases.add(newPhase)
        return this
    }

    override fun withPhases(vararg phases: Phase): PhaseConductor {
        phases.forEach { it.setConductor(this) }
        this.phases.addAll(phases)
        return this
    }

    override fun run() {
        Log.i(TAG, "--- START PHASE CONDUCTING ---")
        phaseIndex = 0
        phases[phaseIndex].execute()
    }

    override fun onAdvance() {
        phaseIndex++
        if (phaseIndex < phases.size) {
            val nextPhase = phases[phaseIndex]
            Log.i(TAG, "--- ADVANCE TO PHASE %s ---".format(nextPhase.name))
            nextPhase.execute()
        }
    }

    override fun onError(failedPhase: Phase) {
        Log.i(TAG, "Phase [%s] failed.".format(failedPhase.name))
        Log.i(TAG, "Executing default error phase")
        defaultErrorPhase.execute()
        Log.i(TAG, "Executing failed phase custom error phase")
        failedPhase.errorPhase?.execute()
    }
}

class FirstErrorAllSuccessStrategy : PhaseStrategy {
    override fun evaluate(phase: Phase): PhaseStrategy.StrategyResult {

        var errorCount = 0
        var successCount = 0
        var incompleteCount = 0
        phase.getTasks().forEach {
            //Log.i(TAG, "ON-COMPLETE RESULT|%s|%s".format(it.name, it.getExecuteResult()))
            when (it.getStatus()) {
                TaskStatus.DONE_WITH_ERROR -> errorCount++
                TaskStatus.DONE_WITH_SUCCESS -> successCount++
                else -> incompleteCount++
            }
        }

//        val isPhaseSuccessful = (incompleteCount <= 0) && (errorCount <= 0) && (successCount > 0)
        if (incompleteCount == 0) {
            val isPhaseSuccessful = (errorCount <= 0) && (successCount > 0)

            val conductor = phase.getConductor()
            if (conductor != null) {
                if (isPhaseSuccessful) {
                    // ADVANCE PHASE
                    conductor.onAdvance()
                } else {
                    // ERROR PHASE
                    conductor.onError(phase)
                }
            }
        }

        return PhaseStrategy.StrategyResult.NOTHING
    }
}

open class DefaultPhase(override var name: String, private var phaseStrategy: PhaseStrategy = FirstErrorAllSuccessStrategy()) : Phase {
    private val TAG = "PhaseFlow.DefaultPhase"
    private var conductor: PhaseConductor? = null
    private var scheduler: Scheduler = Schedulers.from(Executors.newSingleThreadExecutor())
    private val tasks = mutableListOf<Task>()

    override fun getTasks(): MutableList<Task> {
        return tasks
    }

    override fun getConductor(): PhaseConductor? {
        return conductor
    }

    override var errorPhase: Phase? = null

    override fun setConductor(conductor: PhaseConductor): Phase {
        this.conductor = conductor
        return this
    }

    override fun include(newTask: Task): Phase {
        tasks.add(newTask)
        return this
    }

    override fun phaseStrategy(phaseStrategy: PhaseStrategy): Phase {
        this.phaseStrategy = phaseStrategy
        return this
    }

    override fun concurrencyExecutor(executor: Executor): Phase {
        this.scheduler = Schedulers.from(executor)
        return this
    }

    override fun execute() {
        val producers = mutableListOf<Flowable<Task?>?>()
        for (task in tasks) {
            Log.i(TAG, String.format("Queuing Task: %s ", task.name))
            val producer: Flowable<Task?>? = Flowable.fromCallable(task.getExecutableTask()).subscribeOn(this.scheduler)
            producers.add(producer)
            task.onQueued()
        }

        val f = Flowable.merge(producers)
        if (f != null) {
            f.observeOn(AndroidSchedulers.mainThread())
            f.subscribeOn(this.scheduler)
            f.subscribe({ task ->
                //ON-NEXT
                Log.i(TAG, String.format("%s: Task[%s|%s]", "ON-NEXT", task.getExecuteResult(), task.name))
                task.onSuccess()
                evaluatePhase()
            }, { error ->
                //ON-ERROR
                Log.i(TAG, String.format("%s: %s", "ON-ERROR", error.message))
//                conductor?.onError(this)
                evaluatePhase()
            }, {
                //ON-COMPLETE
                Log.i(TAG, "ON-COMPLETE")
            })
        }
    }

    private fun evaluatePhase() {
        when (this.phaseStrategy.evaluate(this)) {
            PhaseStrategy.StrategyResult.SUCCESS-> {}
            PhaseStrategy.StrategyResult.ERROR-> {}
            PhaseStrategy.StrategyResult.NOTHING -> {}
        }
    }
}

class LogStatePhase : DefaultPhase("LOG_ERROR_STATE") {

}

class AbstractTask(override var name: String) : Task {
    private val TAG = "PhaseFlow.AbstractTask"
    private var taskResult: Any? = null
    private var executable: Callable<Task?>? = null
    private var errorRule: (Task) -> (Unit) = { onSuccess() }
    private var taskStatus: TaskStatus = TaskStatus.NOT_EXECUTED
    override fun setExecutableTask(executableTask: Callable<Task?>?): Task {
        this.executable = executableTask
        return this
    }

    override fun getExecutableTask(): Callable<Task?>? {
        return this.executable
    }

    override fun setExecuteResult(result: Any?) {
        this.taskResult = result
    }

    override fun getExecuteResult(): Any? {
        return taskResult
    }

    override fun setErrorRule(errorRule: (Task) -> (Unit)): Task {
        this.errorRule = errorRule
        return this
    }

    override fun onQueued() {
        Log.i(TAG, "%s|onQueued()".format(name))
        taskStatus = TaskStatus.QUEUED

    }

    override fun onSuccess() {
        Log.i(TAG, "%s|onSuccess()".format(name))
        taskStatus = TaskStatus.DONE_WITH_SUCCESS

    }

    override fun onError() {
        Log.i(TAG, "%s|onError()".format(name))
        taskStatus = TaskStatus.DONE_WITH_ERROR
    }

    override fun getStatus(): TaskStatus {
        return this.taskStatus
    }

}