package au.com.beba.chainReaction.feature.decision

import au.com.beba.chainReaction.feature.chain.Chain
import au.com.beba.chainReaction.feature.chain.ChainCallback
import au.com.beba.chainReaction.feature.chain.ChainDecision
import au.com.beba.chainReaction.feature.chain.ChainDecisionListener
import au.com.beba.chainReaction.feature.logger.ConsoleLogger

class BaseChainDecision : ChainDecision {

    @Suppress("PrivatePropertyName")
    private val TAG = BaseChainDecision::class.java.simpleName

    override fun decision(links: List<Chain>, chain: ChainDecisionListener) {
        val decisionTag = "DECISION"
        ConsoleLogger.log(TAG, "{%s}".format(decisionTag))

        //TODO: CHECK "END" CONDITIONS
        val unfinishedLinks = links.count { it.getChainStatus() in listOf(ChainCallback.Status.NOT_STARTED, ChainCallback.Status.IN_PROGRESS) }

        if (unfinishedLinks == 0) {
            ConsoleLogger.log(TAG, "{%s} %s".format(decisionTag, "all chain links have a result"))
            val finalStatus = ChainCallback.Status.SUCCESS // TODO: CALCULATE
            ConsoleLogger.log(TAG, "{%s} notifying parent via chainCallback".format(decisionTag))
            // NOTIFY PARENT chainCallback
            chain.onDecisionDone(finalStatus)
        } else {
            ConsoleLogger.log(TAG, "{%s} not all Links have result yet".format(decisionTag))
            chain.onDecisionNotDone()
        }
    }
}