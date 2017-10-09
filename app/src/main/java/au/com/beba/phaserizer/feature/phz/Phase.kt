package au.com.beba.phaserizer.feature.phz

import android.util.Log
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Callable
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

interface PhaseConductor {
    fun defaultErrorPhase(errorPhase: Phase): PhaseConductor
    fun withPhase(newPhase: Phase): PhaseConductor
    fun withPhases(vararg phases: Phase): PhaseConductor
    fun run()

    fun onAdvance()
    fun onError(failedPhase: Phase)
}

class BasePhaseConductor : PhaseConductor {
    private var TAG = BasePhaseConductor::class.java.simpleName
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
        phaseIndex = 0
        phases[phaseIndex].execute()
    }

    override fun onAdvance() {
        phaseIndex++
        if (phaseIndex < phases.size) {
            phases[phaseIndex].execute()
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

open class DefaultPhase(override var name: String) : Phase {
    private val TAG = DefaultPhase::class.java.simpleName
    private var conductor: PhaseConductor? = null
//    private var scheduler: Scheduler = Schedulers.from(Executors.newSingleThreadExecutor())
//    private var scheduler: Scheduler = Schedulers.from(Executors.newFixedThreadPool(1))
    private var scheduler: Scheduler = Schedulers.io()
    private var stopPolicy: ErrorPolicy = ErrorPolicy.FIRST
    private var successPolicy: SuccessPolicy = SuccessPolicy.ALL
    private val tasks = mutableListOf<Task>()

    override var errorPhase: Phase? = null

    override fun setConductor(conductor: PhaseConductor): Phase {
        this.conductor = conductor
        return this
    }

    override fun include(newTask: Task): Phase {
        tasks.add(newTask)
        return this
    }

    override fun errorPhaseCondition(policy: ErrorPolicy): Phase {
        stopPolicy = policy
        return this
    }

    override fun advancePhaseCondition(policy: SuccessPolicy): Phase {
        successPolicy = policy
        return this
    }

    override fun concurrencyExecutor(executor: Executor): Phase {
        this.scheduler = Schedulers.from(executor)
        return this
    }

    override fun execute() {
        val producers = mutableListOf<Flowable<Task>>()
        for (task in tasks) {
            Log.i(TAG, String.format("Queuing Task: %s ", task.name))
            producers.add(Flowable.fromCallable(task.getExecutableTask()))
        }

        val f = Flowable.merge(producers)
        if (f != null) {
            f.observeOn(AndroidSchedulers.mainThread())
            f.subscribeOn(this.scheduler)
            f.subscribe({ task ->
                //ON-NEXT
                Log.i(TAG, String.format("%s: Task[%s]", "ON-NEXT", task.name))
            }, { error ->
                //ON-ERROR
                Log.i(TAG, String.format("%s: %s", "ON-ERROR", error.message))
                conductor?.onError(this)
            }, {
                //ON-COMPLETE
                Log.i(TAG, "ON-COMPLETE")
                // ADVANCE PHASE
                if (conductor != null) {
                    conductor?.onAdvance()
                }
            })
        }
    }
}


class LogStatePhase : DefaultPhase("LOG_ERROR_STATE") {

}

interface Task {
    var name: String
    fun getExecutableTask(): Callable<Task?>?
    fun setExecutableTask(executableTask: Callable<Task?>?): Task
    fun getExecuteResult(): Any?
    fun setExecuteResult(result: Any?)
    fun critical(): Task
    fun nonCritical(): Task
    fun execute() {

    }

    fun onSuccess()
    fun onError()
}

class AbstractTask(override var name: String) : Task {
    private var taskResult: Any? = null
    private var executable: Callable<Task?>? = null
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

//    fun name(name: String): Task {
//        this.name = name
//        return this
//    }

    override fun critical(): Task {
        return this
    }

    override fun nonCritical(): Task {
        return this
    }

    override fun onSuccess() {}
    override fun onError() {}

    override fun execute() {}
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

fun testBed() {
    val TAG = "testBed"
    val singleThreadExecutor = Executors.newSingleThreadExecutor()
    val parallelThreadExecutor = Executors.newFixedThreadPool(5)

    /**
     * APP LAUNCH
     */
    val launchConductor = BasePhaseConductor()

    val taskLoadConfig = AbstractTask("LOAD_CONFIG").critical()
    taskLoadConfig.setExecutableTask(Callable {
        Log.d(TAG, "Executing task: %s".format(taskLoadConfig.name))
        TimeUnit.SECONDS.sleep(3)
        taskLoadConfig.setExecuteResult(3000)
        taskLoadConfig
    })

    val phaseConfig = DefaultPhase("CONFIG")
            .include(taskLoadConfig)

    /**
     * Phase QuickData
     */
    val taskRefreshQuickBalance = AbstractTask("QUICK_BALANCE").critical()
    taskRefreshQuickBalance.setExecutableTask(Callable {
        Log.d(TAG, "Executing task: %s".format(taskRefreshQuickBalance.name))
        TimeUnit.SECONDS.sleep(4)
        taskRefreshQuickBalance.setExecuteResult(4000)
        taskRefreshQuickBalance
    })
    val taskGetNBA = AbstractTask("LOAD_NBA").nonCritical()
    taskGetNBA.setExecutableTask(Callable {
        Log.d(TAG, "Executing task: %s".format(taskGetNBA.name))
        TimeUnit.SECONDS.sleep(2)
        taskGetNBA.setExecuteResult(2000)
        taskGetNBA
    })
    val taskGetAEM = AbstractTask("LOAD_AEM").nonCritical()
    taskGetAEM.setExecutableTask(Callable {
        Log.d(TAG, "Executing task: %s".format(taskGetAEM.name))
        TimeUnit.SECONDS.sleep(6)
        taskGetAEM.setExecuteResult(6000)
        taskGetAEM
    })
    val phaseQuickData = DefaultPhase("QUICK_DATA")
//            .concurrencyExecutor(parallelThreadExecutor)
            .include(taskRefreshQuickBalance)
            .include(taskGetNBA)
            .include(taskGetAEM)

    launchConductor
            .withPhases(phaseConfig, phaseQuickData)
            .run() // RUN APP LAUNCH

    /**
     * SIGN-IN
     */
    //    val taskSignIn = DefaultTask().name("SIGN_IN").critical()
//
//    val phaseLogin = DefaultPhase()
//            .concurrencyExecutor(singleThreadExecutor)
//            .include(taskSignIn)
//
//    val signInConductor = BasePhaseConductor()

    /**
     * POST SIGN-IN
     */
    //    val phasePostLogin = DefaultPhase()
//    val taskAccountsSummary = DefaultTask().name("ACCOUNTS_SUMMARY").critical()
//    val taskAccountsDetails = DefaultTask().name("ACCOUNTS_DETAILS").nonCritical()
//    phasePostLogin
//            .include(taskAccountsSummary)
//            .include(taskAccountsDetails)
//
//    signInConductor
//            .withPhase(phaseLogin)
//            .withPhase(phasePostLogin)
}