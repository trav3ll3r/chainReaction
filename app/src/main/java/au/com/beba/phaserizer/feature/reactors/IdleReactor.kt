package au.com.beba.phaserizer.feature.reactors

class IdleChainDecision : ChainDecision {
    override fun decision(links: List<Chain>, chain: ChainDecisionListener) {
        chain.onDecisionDone(ChainCallback.Status.SUCCESS)
    }
}