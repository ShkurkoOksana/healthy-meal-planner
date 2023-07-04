package ksu.katara.healthymealplanner.mvvm.views.main.tabs.home.diettips

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import ksu.katara.healthymealplanner.databinding.FragmentDietTipsChaptersBinding
import ksu.katara.healthymealplanner.foundation.model.EmptyResult
import ksu.katara.healthymealplanner.foundation.model.ErrorResult
import ksu.katara.healthymealplanner.foundation.model.PendingResult
import ksu.katara.healthymealplanner.foundation.model.SuccessResult
import ksu.katara.healthymealplanner.foundation.views.BaseFragment
import ksu.katara.healthymealplanner.foundation.views.BaseScreen
import ksu.katara.healthymealplanner.foundation.views.HasScreenTitle
import ksu.katara.healthymealplanner.foundation.views.screenViewModel

class DietTipsChaptersFragment : BaseFragment(), HasScreenTitle {

    /**
     * This screen has not arguments
     */
    class Screen : BaseScreen

    private lateinit var binding: FragmentDietTipsChaptersBinding

    private lateinit var dietTipsChaptersAdapter: DietTipsChaptersAdapter

    override val viewModel by screenViewModel<DietTipsChaptersViewModel>()

    /**
     * Dynamic screen title
     */
    override fun getScreenTitle(): String? = viewModel.screenTitle.value

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDietTipsChaptersBinding.inflate(layoutInflater, container, false)
        initDietTipsRecycleViewSections()
        return binding.root
    }

    private fun initDietTipsRecycleViewSections() {
        dietTipsChaptersAdapter = DietTipsChaptersAdapter(
            requireActivity(),
            viewModel,
        )
        viewModel.dietTipsChapters.observe(viewLifecycleOwner) {
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
        val dietTipsChaptersLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.dietTipsChaptersRecyclerView.layoutManager = dietTipsChaptersLayoutManager
        binding.dietTipsChaptersRecyclerView.adapter = dietTipsChaptersAdapter
    }

    private fun hideAll() = with(binding) {
        dietTipsChaptersRecyclerView.visibility = View.GONE
        dietTipsChaptersProgressBar.visibility = View.GONE
        dietTipsChaptersTryAgainContainer.visibility = View.GONE
        noDietTipsChaptersTextView.visibility = View.GONE
    }

    companion object {
        fun createArgs(screen: BaseScreen) = bundleOf(BaseScreen.ARG_SCREEN to screen)
    }
}
