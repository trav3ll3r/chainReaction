package au.com.beba.chainReaction

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import au.com.beba.chainReaction.testData.*
import au.com.beba.chainreaction.chain.ChainCallback
import au.com.beba.chainreaction.logger.ConsoleLogger
import org.jetbrains.anko.find

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        find<Button>(R.id.btn_test_non_blocking_ui).setOnClickListener { testSuccess() }
        find<Button>(R.id.btn_visualise_chain).setOnClickListener { visualiseChain() }
    }

    private fun testSuccess() {
        val serialReactor = ReactorWithBroadcastIml(this)
        val a = AChain(serialReactor)
        val b = BChain(serialReactor)
        val c = CChain(serialReactor)
        val c1 = C1Chain(serialReactor)
        val c2 = C2Chain(serialReactor)

        a.addToChain(b, c.addToChain(c1, c2))

        val chainReactionCallback = object : ChainCallback {
            override fun onDone(status: ChainCallback.Status) {

                ConsoleLogger.log("--- ASSERT START ---")
                ConsoleLogger.log("--- ASSERT DONE ---")
            }
        }

        ConsoleLogger.log("--- START ---")
        a.startChain(chainReactionCallback)
        ConsoleLogger.log("--- END ---")
    }

    private fun visualiseChain() {
        val intent = Intent(this, VisualiseChainActivity::class.java)
        startActivity(intent)
    }
}