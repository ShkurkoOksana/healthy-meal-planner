package ksu.katara.healthymealplanner.mvvm.views.main.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.databinding.FragmentTabsBinding
import ksu.katara.healthymealplanner.foundation.views.BaseFragment
import ksu.katara.healthymealplanner.foundation.views.BaseScreen
import ksu.katara.healthymealplanner.foundation.views.screenViewModel

class TabsFragment : BaseFragment() {

    /**
     * This screen has not arguments
     */
    class Screen : BaseScreen

    private lateinit var binding: FragmentTabsBinding

    override val viewModel by screenViewModel<TabsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTabsBinding.inflate(layoutInflater, container, false)
        val navHost =
            childFragmentManager.findFragmentById(R.id.tabsFragmentContainer) as NavHostFragment
        val navController = navHost.navController
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)
        return binding.root
    }
}
