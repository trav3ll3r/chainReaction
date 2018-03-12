package au.com.beba.chainReaction

import au.com.beba.chainReaction.testData.*
import au.com.beba.chainreaction.chain.Chain
import au.com.beba.chainreaction.chain.ExecutionStrategy

class MultiParallelChainActivity : BaseVisualChainActivity() {

    override val tag: String = MultiParallelChainActivity::class.java.simpleName

    override fun buildChain(): Chain {
        val parallelReactor = ReactorWithBroadcastIml(this, ExecutionStrategy.PARALLEL)

        val a = AChain(parallelReactor)
        val b = BChain(parallelReactor)
        b.addToChain(
                B1Chain(parallelReactor),
                B2Chain(parallelReactor),
                B3Chain(parallelReactor)
        )

        val c1 = C1Chain(parallelReactor)
        c1.sleepMultiplier = 2
        val c = CChain(parallelReactor)
                .addToChain(
                        c1,
                        C2Chain(parallelReactor),
                        C3Chain(parallelReactor),
                        C4Chain(parallelReactor)
                )

        return a
                .addToChain(
                b
               ,c
        )
    }
}
