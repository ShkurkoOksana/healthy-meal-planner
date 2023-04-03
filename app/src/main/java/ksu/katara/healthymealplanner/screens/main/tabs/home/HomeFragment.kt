package ksu.katara.healthymealplanner.screens.main.tabs.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthymealplanner.R
import com.example.healthymealplanner.databinding.FragmentHomeBinding
import ksu.katara.healthymealplanner.Repositories
import ksu.katara.healthymealplanner.model.dietTips.entities.DietTip
import ksu.katara.healthymealplanner.screens.main.tabs.TabsFragmentDirections
import ksu.katara.healthymealplanner.screens.main.tabs.home.diettips.DietTipsAdapter
import ksu.katara.healthymealplanner.screens.main.tabs.home.diettips.DietTipsViewModel
import ksu.katara.healthymealplanner.tasks.EmptyResult
import ksu.katara.healthymealplanner.tasks.ErrorResult
import ksu.katara.healthymealplanner.tasks.PendingResult
import ksu.katara.healthymealplanner.tasks.SuccessResult
import ksu.katara.healthymealplanner.utils.findTopNavController
import ksu.katara.healthymealplanner.utils.viewModelCreator

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding

    private lateinit var dietTipsAdapter: DietTipsAdapter

    private val dietTipsViewModel by viewModelCreator { DietTipsViewModel(Repositories.dietTipsRepository) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        initDietTipsRecycleViewSection()
    }

    private fun initDietTipsRecycleViewSection() {
        dietTipsAdapter = DietTipsAdapter(dietTipsViewModel)

        dietTipsViewModel.dietTips.observe(viewLifecycleOwner) {
            hideAll()
            when (it) {
                is SuccessResult -> {
                    binding.dietTipsRecyclerView.visibility = View.VISIBLE
                    dietTipsAdapter.dietTips = it.data
                }

                is ErrorResult -> {
                    binding.dietTipTryAgainContainer.visibility = View.VISIBLE
                }

                is PendingResult -> {
                    binding.dietTipsProgressBar.visibility = View.VISIBLE
                }
                is EmptyResult -> {
                    binding.noDietTipsTextView.visibility = View.VISIBLE
                }
            }
        }

        dietTipsViewModel.actionShowDetails.observe(viewLifecycleOwner, Observer {
            it.getValue()?.let { dietTip -> onDietTipPressed(dietTip) }
        })

        val dietTipsLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.dietTipsRecyclerView.layoutManager = dietTipsLayoutManager
        binding.dietTipsRecyclerView.adapter = dietTipsAdapter
    }

    private fun onDietTipPressed(dietTip: DietTip) {
        val dietTipArg = dietTip.id

        val direction =
            TabsFragmentDirections.actionTabsFragmentToDietTipDetailsFragment(dietTipArg)
        findTopNavController().navigate(direction)
    }

    private fun hideAll() = with(binding) {
        dietTipsRecyclerView.visibility = View.GONE
        dietTipsProgressBar.visibility = View.GONE
        dietTipTryAgainContainer.visibility = View.GONE
        noDietTipsTextView.visibility = View.GONE
    }
}