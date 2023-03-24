package ksu.katara.healthymealplanner.screens.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.healthymealplanner.databinding.ActivityMainBinding

/**
 * Container for all screens in the app.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }
        setSupportActionBar(binding.toolbar)
    }
}
