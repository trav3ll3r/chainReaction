package au.com.beba.phaserizer

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import au.com.beba.phaserizer.feature.LocalPreferences
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject lateinit var localPreferences: LocalPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}