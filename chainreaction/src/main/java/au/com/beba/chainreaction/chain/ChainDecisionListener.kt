package au.com.beba.chainreaction.chain

interface ChainWithDecision : Chain {
    fun decisionPhase(): ()->Any?
}

interface ChainDecisionListener {
    fun onDecisionDone(finalStatus: ChainCallback.Status)
    fun onDecisionNotDone()
}