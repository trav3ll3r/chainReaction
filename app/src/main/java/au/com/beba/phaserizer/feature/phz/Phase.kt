package au.com.beba.phaserizer.feature.phz

import android.util.Log
import io.reactivex.Flowable
import io.reactivex.Scheduler
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
        defaultErrorPhase.execute()
        failedPhase.errorPhase?.execute()
    }
}

interface Phase {
    fun setConductor(conductor: PhaseConductor): Phase
    var errorPhase: Phase?
    fun include(newTask: Task): Phase
    fun errorPhaseCondition(policy: ErrorPolicy = ErrorPolicy.FIRST): Phase
    fun advancePhaseCondition(policy: SuccessPolicy = SuccessPolicy.ALL): Phase
    fun concurrencyExecutor(executor: Executor): Phase
    fun execute()
}

open class DefaultPhase : Phase {
    private val TAG = DefaultPhase::class.java.simpleName
    private var conductor: PhaseConductor? = null
    private var scheduler: Scheduler? = null
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
        val sch = this.scheduler
        if (sch != null) {
            val f = Flowable.just(Any())
            for (task in tasks) {
                f.concatMap { Flowable.fromCallable(task.getExecutableTask()) }
            }

            f.subscribeOn(sch)
            f.subscribe({ n ->
                //ON-NEXT
                Log.i(TAG, String.format("%s: %s", n, "ON-NEXT"))
            }, {
                //ON-ERROR
                Log.i(TAG, String.format("%s: %s", "", "ON-ERROR"))
                conductor?.onError(this)
            }, {
                //ON-COMPLETE
                Log.i(TAG, "ON-COMPLETE")
                // ADVANCE PHASE
                if (conductor != null) {
                    conductor?.onAdvance()
                }
            })
        } else {
            throw RuntimeException("Phase Executor cannot be NULL")
        }
    }
}

class LogStatePhase : DefaultPhase() {

}

interface Task{
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

class AbstractTask : Task {
    private var taskResult: Any? = null
    private var name = ""
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

    fun name(name: String): Task {
        this.name = name; return this
    }

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

    val singleThreadExecutor = Executors.newSingleThreadExecutor()
    val parallelThreadExecutor = Executors.newFixedThreadPool(5)

    val taskExecutable: Callable<Task?> = Callable {
        TimeUnit.SECONDS.sleep(3)
        return@Callable null
    }
    /**
     * APP LAUNCH
     */
    val launchConductor = BasePhaseConductor()

    val taskLoadConfig = AbstractTask().name("LOAD_CONFIG").critical()
    taskLoadConfig.setExecutableTask(Callable {
        TimeUnit.SECONDS.sleep(3)
        taskLoadConfig.setExecuteResult(55)
        taskLoadConfig
    })
    
    val taskRefreshQuickBalance = AbstractTask().name("QUICK_BALANCE").critical().setExecutableTask(taskExecutable)
    val taskGetNBA = AbstractTask().name("LOAD_NBA").nonCritical().setExecutableTask(taskExecutable)
    val taskGetAEM = AbstractTask().name("LOAD_AEM").nonCritical().setExecutableTask(taskExecutable)
    val phaseConfig = DefaultPhase()
            .concurrencyExecutor(singleThreadExecutor)
            .include(taskLoadConfig)
    val phaseQuickData = DefaultPhase()
            .concurrencyExecutor(parallelThreadExecutor)
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