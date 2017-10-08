package au.com.beba.phaserizer.feature.phz

import java.math.BigDecimal
import java.util.concurrent.Callable
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

interface PhaseConductor {
    fun defaultErrorPhase(errorPhase: Phase): PhaseConductor
    fun withPhase(newPhase: Phase): PhaseConductor
    fun withPhases(vararg phases: Phase): PhaseConductor
    fun run()
}

class BasePhaseConductor : PhaseConductor {
    private var phases = mutableListOf<Phase>()
    private var phaseIndex = 0
    private var defaultErrorPhase: Phase = LogStatePhase()
    override fun defaultErrorPhase(errorPhase: Phase): PhaseConductor { defaultErrorPhase = errorPhase; return this }
    override fun withPhase(newPhase: Phase): PhaseConductor { phases.add(newPhase); return this }
    override fun withPhases(vararg phases: Phase): PhaseConductor { this.phases.addAll(phases); return this }
    override fun run() {}

    private fun onError(failedPhase: Phase) {
        defaultErrorPhase.execute()
        failedPhase.errorPhase?.execute()
    }
    private fun onAdvance(): Phase? { return if (phaseIndex < phases.size) phases[phaseIndex] else null }
}

interface Phase {
    var errorPhase: Phase?
    fun include(newTask: Task): Phase
    fun errorPhaseCondition(policy: ErrorPolicy = ErrorPolicy.FIRST): Phase
    fun advancePhaseCondition(policy: SuccessPolicy = SuccessPolicy.ALL): Phase
    fun errorPhase(errorPhase: Phase): Phase
    fun concurrencyExecutor(executor: Executor): Phase
    fun execute()
}

open class DefaultPhase : Phase {
    private var executor: Executor? = null
    private var stopPolicy: ErrorPolicy = ErrorPolicy.FIRST
    private var successPolicy: SuccessPolicy = SuccessPolicy.ALL
    override var errorPhase: Phase? = null

    private val tasks = mutableListOf<Task>()
    override fun include(newTask: Task): Phase { tasks.add(newTask); return this }
    override fun errorPhaseCondition(policy: ErrorPolicy): Phase { stopPolicy = policy; return this }
    override fun advancePhaseCondition(policy: SuccessPolicy): Phase { successPolicy = policy; return this }
    override fun errorPhase(errorPhase: Phase): Phase { return this }
    override fun concurrencyExecutor(executor: Executor): Phase { return this }
    override fun execute() {
        if (executor == null) {
            throw RuntimeException("Phase Executor cannot be NULL")
        }
    }
}

class LogStatePhase : DefaultPhase() {

}

interface Task {
    fun setExecutableTask(executableTask: Callable<Any?>?)
    fun getExecuteResult(): Any?
    fun critical(): Task
    fun nonCritical(): Task
    fun execute() {

    }
    fun onSuccess()
    fun onError()
}

abstract class AbstractTask : Task {
    protected var taskResult: Any? = null
    protected var name = ""
    protected var executable: Callable<Any?>? = null
    override fun setExecutableTask(executableTask: Callable<Any?>?) {
        this.executable = executableTask
    }

    override fun getExecuteResult(): Any? {
        return taskResult
    }

    fun name(name: String): Task { this.name = name; return this }
    override fun critical(): Task { return this }
    override fun nonCritical(): Task { return this }
    override fun onSuccess() {}
    override fun onError() {}
}

class LoadConfigTask : AbstractTask() {
//    override fun call() {
//        TimeUnit.SECONDS.sleep(3)
//        taskResult = BigDecimal("11")
//    }
}

class LoadQuickBalanceTask : AbstractTask() {
//    override fun call() {
//        TimeUnit.SECONDS.sleep(3)
//        taskResult = "22.99"
//    }
}

class LoadNBATask : AbstractTask() {
//    override fun call() {
//        TimeUnit.SECONDS.sleep(3)
//        taskResult = 55
//    }
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

fun TestBed() {

//    val singleThreadExecutor = Executors.newSingleThreadExecutor()
//    val parallelThreadExecutor = Executors.newFixedThreadPool(5)
//
//    /**
//     * APP LAUNCH
//     */
//    val launchConductor = BasePhaseConductor()
//
//    val taskLoadConfig = LoadConfigTask().name("LOAD_CONFIG").critical()
//    val taskRefreshQuickBalance = LoadQuickBalanceTask().name("QUICK_BALANCE").critical()
//    val taskGetNBA = LoadNBATask().name("LOAD_NBA").nonCritical()
////    val taskGetAEM = DefaultTask().name("LOAD_AEM").nonCritical()
//    val phaseConfig = DefaultPhase()
//            .concurrencyExecutor(singleThreadExecutor)
//            .include(taskLoadConfig)
//    val phaseQuickData = DefaultPhase()
//            .concurrencyExecutor(parallelThreadExecutor)
//            .include(taskRefreshQuickBalance)
//            .include(taskGetNBA)
////            .include(taskGetAEM)
//    launchConductor
//            .withPhases(phaseConfig, phaseQuickData)
//            .run() // RUN APP LAUNCH
//
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