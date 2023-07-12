package ksu.katara.healthymealplanner.mvvm.views.main.tabs.home.diettips

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import ksu.katara.healthymealplanner.databinding.FragmentDietTipDetailsBinding
import ksu.katara.healthymealplanner.foundation.views.BaseFragment
import ksu.katara.healthymealplanner.foundation.views.BaseScreen
import ksu.katara.healthymealplanner.foundation.views.HasScreenTitle
import ksu.katara.healthymealplanner.foundation.views.onTryAgain
import ksu.katara.healthymealplanner.foundation.views.renderSimpleResult
import ksu.katara.healthymealplanner.foundation.views.screenViewModel

class DietTipDetailsFragment : BaseFragment(), HasScreenTitle {

    /**
     * This screen has 1 argument: id of chosen dietTip
     */
    class Screen(
        val dietTipId: Long
    ) : BaseScreen

    private lateinit var binding: FragmentDietTipDetailsBinding

    override val viewModel by screenViewModel<DietTipDetailsViewModel>()

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
        viewModel.dietTipDetails.observe(viewLifecycleOwner) { result ->
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