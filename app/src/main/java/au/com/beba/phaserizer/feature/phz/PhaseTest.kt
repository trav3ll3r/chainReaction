package au.com.beba.phaserizer.feature.phz

import android.util.Log
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

fun testSuccess() {
    val TAG = "testSuccess"
    val parallelThreadExecutor = Executors.newFixedThreadPool(5)

    /**
     * APP LAUNCH
     */
    val launchConductor = BasePhaseConductor()

    val taskLoadConfig = AbstractTask("LOAD_CONFIG").setErrorRule { it.onError() }
    taskLoadConfig.setExecutableTask(Callable {
        Log.d(TAG, "Executing task: %s".format(taskLoadConfig.name))
        val timeout = 1L
        TimeUnit.SECONDS.sleep(timeout)
        taskLoadConfig.setExecuteResult(timeout)
        taskLoadConfig
    })

    val phaseConfig = DefaultPhase("CONFIG")
            .include(taskLoadConfig)

    /**
     * Phase QuickData
     */
    val taskRefreshQuickBalance = AbstractTask("QUICK_BALANCE").setErrorRule { it.onError() }
    taskRefreshQuickBalance.setExecutableTask(Callable {
        Log.d(TAG, "Executing task: %s".format(taskRefreshQuickBalance.name))
        val timeout = 4L
        TimeUnit.SECONDS.sleep(timeout)
        taskRefreshQuickBalance.setExecuteResult(timeout)
        taskRefreshQuickBalance
    })
    val taskGetNBA = AbstractTask("LOAD_NBA")
    //.nonCritical()
    taskGetNBA.setExecutableTask(Callable {
        Log.d(TAG, "Executing task: %s".format(taskGetNBA.name))
        val timeout = 6L
        TimeUnit.SECONDS.sleep(timeout)
        taskGetNBA.setExecuteResult(timeout)
        taskGetNBA
    })
    val taskGetAEM = AbstractTask("LOAD_AEM")
    //.nonCritical()
    taskGetAEM.setExecutableTask(Callable {
        Log.d(TAG, "Executing task: %s".format(taskGetAEM.name))
        val timeout = 2L
        TimeUnit.SECONDS.sleep(timeout)
        taskGetAEM.setExecuteResult(timeout)
        taskGetAEM
    })
    val phaseQuickData = DefaultPhase("QUICK_DATA")
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

fun testErrorPhase1() {
    val TAG = "testErrorPhase1"
    val parallelThreadExecutor = Executors.newFixedThreadPool(5)

    /**
     * APP LAUNCH
     */
    val launchConductor = BasePhaseConductor()

    val taskLoadConfig = AbstractTask("LOAD_CONFIG").setErrorRule { it.onError() }
    taskLoadConfig.setExecutableTask(Callable {
        Log.d(TAG, "Executing task: %s".format(taskLoadConfig.name))
        val timeout = 1L
        TimeUnit.SECONDS.sleep(timeout)
//        taskLoadConfig.setExecuteResult(timeout)
//        taskLoadConfig
        throw Exception("ERROR DOWNLOADING CONFIG")
    })

    val phaseConfig = DefaultPhase("CONFIG")
            .include(taskLoadConfig)

    /**
     * Phase QuickData
     */
    val taskRefreshQuickBalance = AbstractTask("QUICK_BALANCE").setErrorRule { it.onError() }
    taskRefreshQuickBalance.setExecutableTask(Callable {
        Log.d(TAG, "Executing task: %s".format(taskRefreshQuickBalance.name))
        val timeout = 4L
        TimeUnit.SECONDS.sleep(timeout)
        taskRefreshQuickBalance.setExecuteResult(timeout)
        taskRefreshQuickBalance
    })
    val taskGetNBA = AbstractTask("LOAD_NBA")
    //.nonCritical()
    taskGetNBA.setExecutableTask(Callable {
        Log.d(TAG, "Executing task: %s".format(taskGetNBA.name))
        val timeout = 6L
        TimeUnit.SECONDS.sleep(timeout)
        taskGetNBA.setExecuteResult(timeout)
        taskGetNBA
    })
    val taskGetAEM = AbstractTask("LOAD_AEM")
    //.nonCritical()
    taskGetAEM.setExecutableTask(Callable {
        Log.d(TAG, "Executing task: %s".format(taskGetAEM.name))
        val timeout = 2L
        TimeUnit.SECONDS.sleep(timeout)
        taskGetAEM.setExecuteResult(timeout)
        taskGetAEM
    })
    val phaseQuickData = DefaultPhase("QUICK_DATA")
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

fun testErrorPhase2() {
    val TAG = "testErrorPhase2"
    val parallelThreadExecutor = Executors.newFixedThreadPool(5)

    /**
     * APP LAUNCH
     */
    val launchConductor = BasePhaseConductor()

    val taskLoadConfig = AbstractTask("LOAD_CONFIG").setErrorRule { it.onError() }
    taskLoadConfig.setExecutableTask(Callable {
        Log.d(TAG, "Executing task: %s".format(taskLoadConfig.name))
        val timeout = 1L
        TimeUnit.SECONDS.sleep(timeout)
        taskLoadConfig.setExecuteResult(timeout)
        taskLoadConfig
    })

    val phaseConfig = DefaultPhase("CONFIG")
            .include(taskLoadConfig)

    /**
     * Phase QuickData
     */
    val taskRefreshQuickBalance = AbstractTask("QUICK_BALANCE").setErrorRule { it.onError() }
    taskRefreshQuickBalance.setExecutableTask(Callable {
        Log.d(TAG, "Executing task: %s".format(taskRefreshQuickBalance.name))
        val timeout = 4L
        TimeUnit.SECONDS.sleep(timeout)
//        taskRefreshQuickBalance.setExecuteResult(timeout)
//        taskRefreshQuickBalance
        throw Exception("ERROR REFRESHING QUICK BALANCE")
    })
    val taskGetNBA = AbstractTask("LOAD_NBA")
    //.nonCritical()
    taskGetNBA.setExecutableTask(Callable {
        Log.d(TAG, "Executing task: %s".format(taskGetNBA.name))
        val timeout = 6L
        TimeUnit.SECONDS.sleep(timeout)
        taskGetNBA.setExecuteResult(timeout)
        taskGetNBA
    })
    val taskGetAEM = AbstractTask("LOAD_AEM")
    //.nonCritical()
    taskGetAEM.setExecutableTask(Callable {
        Log.d(TAG, "Executing task: %s".format(taskGetAEM.name))
        val timeout = 2L
        TimeUnit.SECONDS.sleep(timeout)
        taskGetAEM.setExecuteResult(timeout)
        taskGetAEM
    })
    val phaseQuickData = DefaultPhase("QUICK_DATA")
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