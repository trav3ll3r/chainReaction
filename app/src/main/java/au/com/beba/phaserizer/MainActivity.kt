package au.com.beba.phaserizer

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import au.com.beba.phaserizer.feature.phz.testBed
import org.jetbrains.anko.find

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        find<Button>(R.id.btn_test).setOnClickListener { testPhases() }
    }

    private fun testPhases() {
        testBed()
    }
}