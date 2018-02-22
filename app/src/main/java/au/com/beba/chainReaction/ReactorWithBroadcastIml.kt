package au.com.beba.chainReaction

import android.content.Context
import android.support.v4.content.LocalBroadcastManager
import au.com.beba.chainreaction.chain.ExecutionStrategy
import au.com.beba.chainreaction.reactor.BaseReactorWithPhases

class ReactorWithBroadcastIml(context: Context, override val executionStrategy: ExecutionStrategy = ExecutionStrategy.SERIAL) : BaseReactorWithPhases(), ReactorWithBroadcast {
    override val localBroadcast = LocalBroadcastManager.getInstance(context)
}