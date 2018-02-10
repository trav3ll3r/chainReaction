package au.com.beba.chainreaction.reactor

import au.com.beba.chainreaction.chain.ChainDecision
import au.com.beba.chainreaction.chain.Reactor
import au.com.beba.chainreaction.decision.BaseChainDecision

class BaseReactor(override val chainDecision: ChainDecision = BaseChainDecision()) : Reactor