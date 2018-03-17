package au.com.beba.chainReaction

import au.com.beba.chainReaction.testData.*
import au.com.beba.chainreaction.chain.Chain
import au.com.beba.chainreaction.chain.ExecutionStrategy

class StopForUiChainActivity : BaseVisualChainActivity() {

    override val tag: String = StopForUiChainActivity::class.java.simpleName

    override fun onStart() {
        super.onStart()
        showAppUi()
    }

    override fun buildChain(): Chain {
        val serialReactor = ReactorWithBroadcastIml(this, ExecutionStrategy.SERIAL)

        val a = AChain(serialReactor)
        val b = BChain(serialReactor)

        return a
                .addToChain(
                        b.addToChain(AnyLetterChain(serialReactor), AnyNumberChain(serialReactor)),
                        CChain(serialReactor)
                )
    }
}
