package ksu.katara.healthymealplanner.mvvm

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.databinding.ActivityMainBinding
import ksu.katara.healthymealplanner.foundation.ActivityScopeViewModel
import ksu.katara.healthymealplanner.foundation.navigator.StackFragmentNavigator
import ksu.katara.healthymealplanner.foundation.views.FragmentsHolder
import ksu.katara.healthymealplanner.mvvm.views.main.tabs.TabsFragment

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), FragmentsHolder {

    private val activityViewModel by viewModels<ActivityScopeViewModel>()

    private lateinit var navigator: StackFragmentNavigator

    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }
        navController = getRootNavController()
        navigator = StackFragmentNavigator(
            activity = this,
            navController = navController,
            toolbarId = R.id.toolbar,
            defaultTitle = getString(R.string.app_name),
            animations = StackFragmentNavigator.Animations(
                enterAnim = R.anim.enter,
                exitAnim = R.anim.exit,
                popEnterAnim = R.anim.pop_enter,
                popExitAnim = R.anim.pop_exit
            ),
            initialScreenCreator = { TabsFragment.Screen() }
        )
        navigator.onCreate()
    }

    private fun getRootNavController(): NavController {
        val navHost =
            supportFragmentManager.findFragmentById(R.id.mainFragmentContainer) as NavHostFragment
        return navHost.navController
    }

    override fun onDestroy() {
        navController = null
        navigator.onDestroy()
        super.onDestroy()
    }

    override fun onBackPressed() {
        navigator.onBackPressed()
        super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean =
        (navController?.navigateUp() ?: false) || super.onSupportNavigateUp()

    override fun onResume() {
        super.onResume()
        activityViewModel.navigator.setTarget(navigator)
    }

    override fun onPause() {
        super.onPause()
        activityViewModel.navigator.setTarget(null)
    }

    override fun notifyScreenUpdates() {
        navigator.notifyScreenUpdates()
    }

    override fun getActivityScopeViewModel(): ActivityScopeViewModel {
        return activityViewModel
    }
}