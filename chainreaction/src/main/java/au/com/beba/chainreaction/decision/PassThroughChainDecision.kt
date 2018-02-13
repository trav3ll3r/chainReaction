package au.com.beba.chainreaction.decision

import au.com.beba.chainreaction.chain.Chain
import au.com.beba.chainreaction.chain.ChainCallback
import au.com.beba.chainreaction.chain.ChainDecision
import au.com.beba.chainreaction.chain.ChainDecisionListener

class PassThroughChainDecision : ChainDecision {
    override fun decision(links: List<Chain>, mainTaskStatus: ChainCallback.Status, decisionListener: ChainDecisionListener) {
        decisionListener.onDecisionDone(ChainCallback.Status.SUCCESS)
    }
}