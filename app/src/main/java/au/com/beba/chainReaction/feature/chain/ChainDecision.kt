package au.com.beba.chainReaction.feature.chain

interface ChainDecision {
    fun decision(links: List<Chain>, chain : ChainDecisionListener)
}