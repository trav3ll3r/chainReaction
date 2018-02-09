package au.com.beba.chainreaction.chain

interface ChainDecision {
    fun decision(links: List<Chain>, chain: ChainDecisionListener)
}