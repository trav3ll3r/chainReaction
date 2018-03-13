package au.com.beba.chainReaction

import au.com.beba.chainReaction.testData.AChain
import au.com.beba.chainReaction.testData.AnyLetterChain
import au.com.beba.chainReaction.testData.BChain
import au.com.beba.chainReaction.testData.CChain
import au.com.beba.chainreaction.chain.Chain
import au.com.beba.chainreaction.chain.ExecutionStrategy

class StopForUiChainActivity : BaseVisualChainActivity() {

    override val tag: String = StopForUiChainActivity::class.java.simpleName

    override fun buildChain(): Chain {
        val serialReactor = ReactorWithBroadcastIml(this, ExecutionStrategy.SERIAL)

        val a = AChain(serialReactor)
        val b = BChain(serialReactor)


        return a
                .addToChain(
                        b.addToChain(AnyLetterChain(serialReactor)),
                        CChain(serialReactor)
                )
    }
}
