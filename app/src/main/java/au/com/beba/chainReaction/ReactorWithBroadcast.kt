package au.com.beba.chainReaction

import android.support.v4.content.LocalBroadcastManager
import au.com.beba.chainreaction.chain.Reactor

interface ReactorWithBroadcast : Reactor {
    val localBroadcast: LocalBroadcastManager?
}