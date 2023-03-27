package ksu.katara.healthymealplanner

import android.os.Bundle
import ksu.katara.healthymealplanner.screens.splash.SplashFragment
import androidx.appcompat.app.AppCompatActivity
import com.example.healthymealplanner.R

/**
 * Entry point of the app.
 *
 * Splash activity contains only window background, all other initialization logic is placed to
 * [SplashFragment].
 */
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }

}
