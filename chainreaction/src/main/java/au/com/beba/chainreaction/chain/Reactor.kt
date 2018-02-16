package au.com.beba.chainreaction.chain

import java.util.concurrent.ExecutorService

interface Reactor {
    val chainDecision: ChainDecision
    val chainExecutor: ExecutorService
}