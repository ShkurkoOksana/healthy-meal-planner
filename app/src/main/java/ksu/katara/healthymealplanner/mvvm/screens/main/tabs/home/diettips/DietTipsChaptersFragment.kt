package ksu.katara.healthymealplanner.mvvm.screens.main.tabs.home.diettips

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.databinding.FragmentDietTipsChaptersBinding
import ksu.katara.healthymealplanner.mvvm.Repositories
import ksu.katara.healthymealplanner.mvvm.model.dietTips.entities.DietTip
import ksu.katara.healthymealplanner.mvvm.tasks.EmptyResult
import ksu.katara.healthymealplanner.mvvm.tasks.ErrorResult
import ksu.katara.healthymealplanner.mvvm.tasks.PendingResult
import ksu.katara.healthymealplanner.mvvm.tasks.SuccessResult
import ksu.katara.healthymealplanner.mvvm.utils.findTopNavController
import ksu.katara.healthymealplanner.mvvm.utils.viewModelCreator

class DietTipsChaptersFragment : Fragment(R.layout.fragment_diet_tips_chapters) {

    private lateinit var binding: FragmentDietTipsChaptersBinding

    private lateinit var dietTipsChaptersAdapter: DietTipsChaptersAdapter

    private val dietTipsChaptersViewModel by viewModelCreator { DietTipsChaptersViewModel(Repositories.dietTipsRepository) }
    private val dietTipsViewModel by viewModelCreator { DietTipsViewModel(Repositories.dietTipsRepository) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDietTipsChaptersBinding.bind(view)

        initDietTipsRecycleViewSections()
    }

    private fun initDietTipsRecycleViewSections() {
        dietTipsChaptersAdapter = DietTipsChaptersAdapter(
            requireActivity(),
            dietTipsViewModel,
        )
        dietTipsChaptersViewModel.dietTipsChapters.observe(viewLifecycleOwner) {
            hideAll()
            when (it) {
                is SuccessResult -> {
                    dietTipsChaptersAdapter.dietTipsChapters = it.data
                    binding.dietTipsChaptersRecyclerView.visibility = View.VISIBLE
                }

                is ErrorResult -> {
                    binding.dietTipsChaptersTryAgainContainer.visibility = View.VISIBLE
                }

                is PendingResult -> {
                    binding.dietTipsChaptersProgressBar.visibility = View.VISIBLE
                }

                is EmptyResult -> {
                    binding.noDietTipsChaptersTextView.visibility = View.VISIBLE
                }
            }
        }
        dietTipsViewModel.actionShowToast.observe(viewLifecycleOwner) {
            it.getValue()?.let { messageRes -> Toast.makeText(requireContext(), messageRes, Toast.LENGTH_SHORT).show() }
        }
        dietTipsViewModel.actionShowDetails.observe(viewLifecycleOwner) {
            it.getValue()?.let { dietTip -> onDietTipPressed(dietTip) }
        }
        val dietTipsChaptersLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.dietTipsChaptersRecyclerView.layoutManager = dietTipsChaptersLayoutManager
        binding.dietTipsChaptersRecyclerView.adapter = dietTipsChaptersAdapter
    }

    private fun onDietTipPressed(dietTip: DietTip) {
        val dietTipArg = dietTip.id
        val direction = DietTipsChaptersFragmentDirections.actionDietTipsChaptersFragmentToDietTipDetailsFragment(dietTipArg)
        findTopNavController().navigate(direction)
    }

    private fun hideAll() = with(binding) {
        dietTipsChaptersRecyclerView.visibility = View.GONE
        dietTipsChaptersProgressBar.visibility = View.GONE
        dietTipsChaptersTryAgainContainer.visibility = View.GONE
        noDietTipsChaptersTextView.visibility = View.GONE
    }
}
