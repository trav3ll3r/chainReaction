package au.com.beba.chainReaction

import au.com.beba.chainReaction.testData.*
import au.com.beba.chainreaction.chain.Chain
import au.com.beba.chainreaction.chain.ExecutionStrategy

class VisualiseChainActivity : BaseVisualChainActivity() {

    override val tag: String = VisualiseChainActivity::class.java.simpleName

    override fun buildChain(): Chain {
        val serialReactor = ReactorWithBroadcastIml(this, ExecutionStrategy.SERIAL)
        val parallelReactor = ReactorWithBroadcastIml(this, ExecutionStrategy.PARALLEL)

        val a = AChain(serialReactor)
        val b = BChain(serialReactor)
        b.addToChain(B1Chain(serialReactor))

        val c1 = C1Chain(serialReactor)
        c1.sleepMultiplier = 2
        val c = CChain(parallelReactor)
                .addToChain(c1, C2Chain(serialReactor))
        val d = DChain(serialReactor)
        val e = EChain(serialReactor).addToChain(E1Chain(serialReactor))
        val f = FChain(serialReactor)
        return a
                .addToChain(
                b
               ,c
               ,d
               ,e
               ,f
        )
    }
}
