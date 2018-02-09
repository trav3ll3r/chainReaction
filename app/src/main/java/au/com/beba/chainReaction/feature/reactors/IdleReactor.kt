package au.com.beba.chainReaction.feature.reactors

import au.com.beba.chainReaction.feature.chain.Reactor

class IdleReactor(override val chainDecision: ChainDecision = IdleChainDecision()) : Reactor

class IdleChainDecision : ChainDecision {
    override fun decision(links: List<Chain>, chain: ChainDecisionListener) {
        chain.onDecisionDone(ChainCallback.Status.SUCCESS)
    }
}