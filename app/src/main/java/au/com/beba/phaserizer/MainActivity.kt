package au.com.beba.phaserizer

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import au.com.beba.phaserizer.feature.phz.testErrorPhase1
import au.com.beba.phaserizer.feature.phz.testErrorPhase2
import au.com.beba.phaserizer.feature.phz.testSuccess
import org.jetbrains.anko.find

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        find<Button>(R.id.btn_test_success).setOnClickListener { testSuccess() }
        find<Button>(R.id.btn_test_error_phase1).setOnClickListener { testErrorPhase1() }
        find<Button>(R.id.btn_test_error_phase2).setOnClickListener { testErrorPhase2() }
    }

}