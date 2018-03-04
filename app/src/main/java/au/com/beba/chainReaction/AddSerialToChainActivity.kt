package au.com.beba.chainReaction

import au.com.beba.chainReaction.testData.*
import au.com.beba.chainreaction.chain.Chain
import au.com.beba.chainreaction.chain.ExecutionStrategy
import au.com.beba.chainreaction.chain.Reaction
import au.com.beba.chainreaction.logger.ConsoleLogger

class AddSerialToChainActivity : BaseVisualChainActivity() {

    override val tag: String = AddSerialToChainActivity::class.java.simpleName

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

        val e1 = E1Chain(serialReactor)
                e1.addReaction(Reaction("NEW_LINK", { chain, r ->
                    ConsoleLogger.log("REACTION", "Add link E2 to %s".format(chain::class.java.simpleName))
                    chain.addToChain(E2Chain(chain.reactor))
                    r.skip = true
                })
                )
        val e = EChain(serialReactor)
                .addToChain(e1)
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
