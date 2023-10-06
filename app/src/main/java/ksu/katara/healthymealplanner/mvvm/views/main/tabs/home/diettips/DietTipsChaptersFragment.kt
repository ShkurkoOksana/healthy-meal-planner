package ksu.katara.healthymealplanner.mvvm.views.main.tabs.home.diettips

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import ksu.katara.healthymealplanner.databinding.FragmentDietTipsChaptersBinding
import ksu.katara.healthymealplanner.foundation.views.BaseFragment
import ksu.katara.healthymealplanner.foundation.views.BaseScreen
import ksu.katara.healthymealplanner.foundation.views.HasScreenTitle
import ksu.katara.healthymealplanner.foundation.views.onTryAgain
import ksu.katara.healthymealplanner.foundation.views.renderSimpleResult
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
        initView()
        return binding.root
    }

    private fun initView() {
        dietTipsChaptersAdapter = DietTipsChaptersAdapter(
            requireActivity(),
            viewModel,
        )
        viewModel.dietTipsChapters.observe(viewLifecycleOwner) { result ->
            renderSimpleResult(
                root = binding.root,
                result = result,
                onSuccess = {
                    dietTipsChaptersAdapter.dietTipsChapters = it
                }
            )
        }
        onTryAgain(binding.root) {
            viewModel.tryAgain()
        }
        val dietTipsChaptersLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.dietTipChaptersRecyclerView.layoutManager = dietTipsChaptersLayoutManager
        binding.dietTipChaptersRecyclerView.adapter = dietTipsChaptersAdapter
    }

    companion object {
        fun createArgs(screen: BaseScreen) = bundleOf(BaseScreen.ARG_SCREEN to screen)
    }
}
