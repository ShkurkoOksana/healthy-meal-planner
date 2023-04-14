package ksu.katara.healthymealplanner.screens.main.tabs.home.diettips

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthymealplanner.R
import com.example.healthymealplanner.databinding.FragmentDietTipDetailsMoreBinding
import ksu.katara.healthymealplanner.Repositories
import ksu.katara.healthymealplanner.model.dietTips.entities.DietTip
import ksu.katara.healthymealplanner.tasks.EmptyResult
import ksu.katara.healthymealplanner.tasks.ErrorResult
import ksu.katara.healthymealplanner.tasks.PendingResult
import ksu.katara.healthymealplanner.tasks.SuccessResult
import ksu.katara.healthymealplanner.utils.findTopNavController
import ksu.katara.healthymealplanner.utils.viewModelCreator

class DietTipDetailsMoreFragment : Fragment(R.layout.fragment_diet_tip_details_more) {

    private lateinit var binding: FragmentDietTipDetailsMoreBinding

    private lateinit var dietTipsChaptersAdapter: DietTipsChaptersAdapter

    private val dietTipsChaptersViewModel by viewModelCreator { DietTipsChaptersViewModel(Repositories.dietTipsRepository) }
    private val dietTipsViewModel by viewModelCreator { DietTipsViewModel(Repositories.dietTipsRepository) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDietTipDetailsMoreBinding.bind(view)

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

        val direction = DietTipDetailsMoreFragmentDirections.actionDietTipDetailsMoreFragmentToDietTipDetailsFragment(dietTipArg)
        findTopNavController().navigate(direction)
    }

    private fun hideAll() = with(binding) {
        dietTipsChaptersRecyclerView.visibility = View.GONE
        dietTipsChaptersProgressBar.visibility = View.GONE
        dietTipsChaptersTryAgainContainer.visibility = View.GONE
        noDietTipsChaptersTextView.visibility = View.GONE
    }
}
