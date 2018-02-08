package au.com.beba.phaserizer.feature.reactors

class IdleReactor(override val chainDecision: ChainDecision = IdleChainDecision()) : Reactor

class IdleChainDecision : ChainDecision {
    override fun decision(links: List<Chain>, chain: ChainDecisionListener) {
        chain.onDecisionDone(ChainCallback.Status.SUCCESS)
    }
}