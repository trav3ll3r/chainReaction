package au.com.beba.chainreaction.decision

import au.com.beba.chainreaction.chain.Chain
import au.com.beba.chainreaction.chain.ChainCallback
import au.com.beba.chainreaction.chain.ChainDecision
import au.com.beba.chainreaction.logger.ConsoleLogger

class BaseChainWithPhasesDecision : ChainDecision {

    @Suppress("PrivatePropertyName")
    private val TAG = BaseChainWithPhasesDecision::class.java.simpleName

    override fun decision(links: List<Chain>, mainTaskStatus: ChainCallback.Status): ChainCallback.Status {
        ConsoleLogger.log(TAG, "decisionPhase")
        //TODO: CHECK "END" CONDITIONS
        // TODO: INJECT STRATEGY?
        val failedLinks = links.count { it.getChainStatus() == ChainCallback.Status.ERROR }
        if (mainTaskStatus == ChainCallback.Status.SUCCESS && failedLinks == 0) {
            return ChainCallback.Status.SUCCESS
        }
        return ChainCallback.Status.ERROR
    }
}