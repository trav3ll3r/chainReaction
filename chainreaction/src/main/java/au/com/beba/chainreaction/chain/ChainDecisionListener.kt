package au.com.beba.chainreaction.chain

import au.com.beba.chainreaction.chain.ChainCallback

interface ChainDecisionListener {
    fun onDecisionDone(finalStatus: ChainCallback.Status)
    fun onDecisionNotDone()
}