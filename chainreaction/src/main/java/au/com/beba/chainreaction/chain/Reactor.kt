package au.com.beba.chainreaction.chain

interface Reactor {
    val chainDecision: ChainDecision
    val executionStrategy: ExecutionStrategy
    val chainStepSwitcher: ChainStepSwitcher
}