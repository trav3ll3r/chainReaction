package au.com.beba.chainReaction

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import au.com.beba.chainReaction.testData.*
import au.com.beba.chainreaction.chain.Chain
import au.com.beba.chainreaction.chain.ChainCallback
import au.com.beba.chainreaction.logger.ConsoleLogger
import org.jetbrains.anko.find
import java.util.concurrent.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        find<Button>(R.id.btn_static_mixed).setOnClickListener { staticMixed() }
        find<Button>(R.id.btn_add_serial).setOnClickListener { addSerial() }
    }

    private fun staticMixed() {
        val intent = Intent(this, StaticMixedChainActivity::class.java)
        startActivity(intent)
    }

    private fun addSerial() {
        val intent = Intent(this, AddSerialToChainActivity::class.java)
        startActivity(intent)
    }

//    private fun testSuccess() {
//        val serialReactor = ReactorWithBroadcastIml(this)
//        val a = AChain(serialReactor)
//        val b = BChain(serialReactor)
//        val c = CChain(serialReactor)
//        val c1 = C1Chain(serialReactor)
//        val c2 = C2Chain(serialReactor)
//
//        a.addToChain(b, c.addToChain(c1, c2))
//        a.setParentCallback(object : ChainCallback<Chain> {
//            override fun onDone(completedChain: Chain) {
//                ConsoleLogger.log("--- ASSERT START ---")
//                ConsoleLogger.log("--- ASSERT DONE ---")
//            }
//        })
//
//        ConsoleLogger.log("--- START ---")
//        val chainExecutor: ExecutorService = Executors.newSingleThreadExecutor()
//        val f: Future<Any?> = chainExecutor.submit(a)
//        while (!f.isDone) {
//            try {
//                f.get(10, TimeUnit.MILLISECONDS)
//            } catch (ignore: TimeoutException) {}
//        }
//        ConsoleLogger.log("--- END ---")
//    }
}