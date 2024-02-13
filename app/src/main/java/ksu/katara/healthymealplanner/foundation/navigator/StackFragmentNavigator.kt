package ksu.katara.healthymealplanner.foundation.navigator

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.annotation.AnimRes
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import com.google.android.material.appbar.MaterialToolbar
import ksu.katara.healthymealplanner.foundation.utils.Event
import ksu.katara.healthymealplanner.foundation.views.BaseFragment
import ksu.katara.healthymealplanner.foundation.views.BaseScreen
import ksu.katara.healthymealplanner.foundation.views.HasScreenTitle

class StackFragmentNavigator(
    private val activity: AppCompatActivity,
    private val navController: NavController?,
    @IdRes private val toolbarId: Int,
    private val defaultTitle: String,
    private val animations: Animations,
    private val initialScreenCreator: () -> BaseScreen
) : Navigator {

    private var result: Event<Any>? = null

    private var currentFragment: Fragment? = null

    override fun launch(destinationId: Int, args: Bundle?) {
        launchDestination(destinationId, args)
    }

    override fun goBack(result: Any?) {
        if (result != null) {
            this.result = Event(result)
        }
        activity.onBackPressed()
    }

    fun onCreate() {
        activity.supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentCallbacks, true)
    }

    fun onDestroy() {
        activity.supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentCallbacks)
    }

    fun onBackPressed() {
        val f = getCurrentFragment()
        if (f is BaseFragment) {
            f.viewModel.onBackPressed()
        }
    }

    fun notifyScreenUpdates() {
        setupActionBar()
        setFullScreen()
    }

    private fun setupActionBar() {
        val toolbar = activity.findViewById<MaterialToolbar>(toolbarId)
        activity.setSupportActionBar(toolbar)
        val f = getCurrentFragment()
        if (navController?.currentDestination?.id == navController?.graph?.startDestinationId) {
            // more than 1 screen -> show back button in the toolbar
            activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        } else {
            activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        if (f is HasScreenTitle && f.getScreenTitle() != null) {
            activity.supportActionBar?.title = f.getScreenTitle()
        }
    }

    private fun setFullScreen() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            activity.window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }


    private fun launchDestination(destinationId: Int, args: Bundle? = null) {
        navController?.navigate(
            destinationId,
            args,
            navOptions {
                anim {
                    enter = animations.enterAnim
                    exit = animations.exitAnim
                    popEnter = animations.popEnterAnim
                    popExit = animations.popExitAnim
                }
            }
        )
    }

    private fun publishResults(fragment: Fragment) {
        val result = result?.get() ?: return
        if (fragment is BaseFragment) {
            // has result that can be delivered to the screen's view-model
            fragment.viewModel.onResult(result)
        }
    }

    private fun getCurrentFragment() = currentFragment

    private val fragmentCallbacks = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentViewCreated(
            fm: FragmentManager,
            f: Fragment,
            v: View,
            savedInstanceState: Bundle?
        ) {
            if (f is NavHostFragment) return
            currentFragment = f
            notifyScreenUpdates()
            publishResults(f)
        }
    }

    class Animations(
        @AnimRes val enterAnim: Int,
        @AnimRes val exitAnim: Int,
        @AnimRes val popEnterAnim: Int,
        @AnimRes val popExitAnim: Int,
    )
}