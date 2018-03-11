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
        val b1 = B1Chain(serialReactor)
        val c = CChain(parallelReactor)

        b1.addReaction(
                Reaction(
                        "NEW_LINK",
                        { chain, r ->
                            ConsoleLogger.log("REACTION", "Add link B2 to %s".format(chain::class.java.simpleName))
                            chain.addToChain(B2Chain(chain.reactor))
                            r.skip = true
                        })
        )

        return a
                .addToChain(
                        b.addToChain(b1),
                        c
                )
    }
}
