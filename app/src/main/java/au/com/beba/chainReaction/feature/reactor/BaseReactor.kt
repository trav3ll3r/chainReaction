package au.com.beba.chainReaction.feature.reactor

import au.com.beba.chainReaction.feature.chain.ChainDecision
import au.com.beba.chainReaction.feature.chain.Reactor
import au.com.beba.chainReaction.feature.decision.BaseChainDecision

class BaseReactor(override val chainDecision: ChainDecision = BaseChainDecision()) : Reactor