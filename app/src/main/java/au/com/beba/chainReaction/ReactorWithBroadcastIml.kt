package au.com.beba.chainReaction

import android.content.Context
import android.support.v4.content.LocalBroadcastManager
import au.com.beba.chainreaction.reactor.BaseReactorWithPhases
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ReactorWithBroadcastIml(context: Context, override val chainExecutor: ExecutorService = Executors.newSingleThreadExecutor()) : BaseReactorWithPhases(), ReactorWithBroadcast {
    override val localBroadcast = LocalBroadcastManager.getInstance(context)
}