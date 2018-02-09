package au.com.beba.chainReaction.feature.chain

interface ChainDecisionListener {
    fun onDecisionDone(finalStatus: ChainCallback.Status)
    fun onDecisionNotDone()
}