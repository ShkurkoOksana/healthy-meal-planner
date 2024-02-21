package ksu.katara.healthymealplanner.mvvm.presentation.main.tabs.home.diettips

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import dagger.hilt.android.AndroidEntryPoint
import ksu.katara.healthymealplanner.databinding.FragmentDietTipDetailsBinding
import ksu.katara.healthymealplanner.foundation.utils.viewModelCreator
import ksu.katara.healthymealplanner.foundation.views.BaseFragment
import ksu.katara.healthymealplanner.foundation.views.BaseScreen
import ksu.katara.healthymealplanner.foundation.views.HasScreenTitle
import ksu.katara.healthymealplanner.foundation.views.onTryAgain
import ksu.katara.healthymealplanner.foundation.views.renderSimpleResult
import javax.inject.Inject

@AndroidEntryPoint
class DietTipDetailsFragment : BaseFragment(), HasScreenTitle {

    /**
     * This screen has 1 argument: id of chosen dietTip
     */
    class Screen(
        val dietTipId: Long
    ) : BaseScreen

    private lateinit var binding: FragmentDietTipDetailsBinding

    @Inject
    lateinit var factory: DietTipDetailsViewModel.Factory

    override val viewModel by viewModelCreator<DietTipDetailsViewModel> {
        factory.create(requireArguments().getSerializable(BaseScreen.ARG_SCREEN) as BaseScreen)
    }

    override fun getScreenTitle(): String? = viewModel.screenTitle.value

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDietTipDetailsBinding.inflate(layoutInflater, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        viewModel.dietTipDetailsSteps.observe(viewLifecycleOwner) { result ->
            renderSimpleResult(
                root = binding.root,
                result = result,
                onSuccess = {
                    val dietTipDetailsViewPagerAdapter = DietTipDetailsViewPagerAdapter(it)
                    binding.dietTipDetailsViewPager.adapter = dietTipDetailsViewPagerAdapter
                }
            )
        }
        onTryAgain(binding.root) {
            viewModel.tryAgain()
        }
    }

    companion object {
        fun createArgs(screen: BaseScreen) = bundleOf(BaseScreen.ARG_SCREEN to screen)
    }
}