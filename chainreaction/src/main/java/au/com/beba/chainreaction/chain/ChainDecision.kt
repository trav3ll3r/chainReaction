package au.com.beba.chainreaction.chain

interface ChainDecision {
    fun decision(links: List<Chain>, mainTaskStatus: ChainCallback.Status, decisionListener: ChainDecisionListener)
}