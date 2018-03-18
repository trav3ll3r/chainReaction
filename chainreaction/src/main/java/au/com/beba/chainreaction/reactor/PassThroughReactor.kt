package au.com.beba.chainreaction.reactor

import au.com.beba.chainreaction.chain.ChainDecision
import au.com.beba.chainreaction.chain.ChainStepSwitcher
import au.com.beba.chainreaction.chain.ExecutionStrategy
import au.com.beba.chainreaction.chain.Reactor
import au.com.beba.chainreaction.decision.PassThroughChainDecision
import au.com.beba.chainreaction.stepSwitcher.BaseChainStepSwitcher

class PassThroughReactor(
        override val chainDecision: ChainDecision = PassThroughChainDecision(),
        override val executionStrategy: ExecutionStrategy = ExecutionStrategy.SERIAL,
        override val chainStepSwitcher: ChainStepSwitcher = BaseChainStepSwitcher()
) : Reactor