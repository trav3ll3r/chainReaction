package au.com.beba.chainreaction.chain

import java.util.concurrent.Executor

interface Reactor {
    val chainDecision: ChainDecision
    val chainExecutor: Executor
}