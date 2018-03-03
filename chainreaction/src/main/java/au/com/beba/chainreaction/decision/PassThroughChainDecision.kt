package au.com.beba.chainreaction.decision

import au.com.beba.chainreaction.chain.Chain
import au.com.beba.chainreaction.chain.ChainCallback
import au.com.beba.chainreaction.chain.ChainDecision

class PassThroughChainDecision : ChainDecision {
    override fun decision(links: List<Chain>, mainTaskStatus: ChainCallback.Status)
            : ChainCallback.Status = ChainCallback.Status.SUCCESS
}