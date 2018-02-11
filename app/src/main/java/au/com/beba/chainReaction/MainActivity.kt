package au.com.beba.chainReaction

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

        find<Button>(R.id.btn_test_success).setOnClickListener { testSuccess() }
//        find<Button>(R.id.btn_test_error_phase1).setOnClickListener { testErrorPhase1() }
//        find<Button>(R.id.btn_test_error_phase2).setOnClickListener { testErrorPhase2() }
    }

    private fun testSuccess() {
        val a = AChain()
        val b = BChain()
        val c = CChain()
        val c1 = C1Chain()
        val c2 = C2Chain()

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
}