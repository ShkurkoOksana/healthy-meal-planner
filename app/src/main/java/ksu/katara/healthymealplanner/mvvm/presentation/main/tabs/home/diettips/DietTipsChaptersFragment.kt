package ksu.katara.healthymealplanner.mvvm.presentation.main.tabs.home.diettips

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ksu.katara.healthymealplanner.databinding.FragmentDietTipsChaptersBinding
import ksu.katara.healthymealplanner.foundation.views.BaseFragment
import ksu.katara.healthymealplanner.foundation.views.BaseScreen
import ksu.katara.healthymealplanner.foundation.views.HasScreenTitle
import ksu.katara.healthymealplanner.foundation.views.onTryAgain
import ksu.katara.healthymealplanner.foundation.views.renderSimpleResult

@AndroidEntryPoint
class DietTipsChaptersFragment : BaseFragment(), HasScreenTitle {

    /**
     * This screen has not arguments
     */
    class Screen : BaseScreen

    private lateinit var binding: FragmentDietTipsChaptersBinding
    private lateinit var chapterDietTipsListAdapter: ChapterDietTipsListAdapter

    override val viewModel by viewModels<DietTipsChaptersViewModel>()

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
        initView()
        return binding.root
    }

    private fun initView() {
        chapterDietTipsListAdapter = ChapterDietTipsListAdapter(
            requireActivity(),
            viewModel
        )
        viewModel.chapterDietTipsList.observe(viewLifecycleOwner) { result ->
            renderSimpleResult(
                root = binding.root,
                result = result,
                onSuccess = {
                    chapterDietTipsListAdapter.chapterDietTipsList = it
                }
            )
        }
        onTryAgain(binding.root) {
            viewModel.tryAgain()
        }
        val chapterDietTipsListLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.dietTipChaptersRecyclerView.layoutManager = chapterDietTipsListLayoutManager
        binding.dietTipChaptersRecyclerView.adapter = chapterDietTipsListAdapter
    }

    companion object {
        fun createArgs(screen: BaseScreen) = bundleOf(BaseScreen.ARG_SCREEN to screen)
    }
}
