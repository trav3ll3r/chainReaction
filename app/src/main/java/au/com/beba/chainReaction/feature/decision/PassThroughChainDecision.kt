package au.com.beba.chainReaction.feature.decision

import au.com.beba.chainReaction.feature.chain.Chain
import au.com.beba.chainReaction.feature.chain.ChainCallback
import au.com.beba.chainReaction.feature.chain.ChainDecision
import au.com.beba.chainReaction.feature.chain.ChainDecisionListener

class PassThroughChainDecision : ChainDecision {
    override fun decision(links: List<Chain>, chain: ChainDecisionListener) {
        chain.onDecisionDone(ChainCallback.Status.SUCCESS)
    }
}