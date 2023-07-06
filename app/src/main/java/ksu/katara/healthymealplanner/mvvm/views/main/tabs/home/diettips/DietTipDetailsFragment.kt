package ksu.katara.healthymealplanner.mvvm.views.main.tabs.home.diettips

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import ksu.katara.healthymealplanner.databinding.FragmentDietTipDetailsBinding
import ksu.katara.healthymealplanner.databinding.PartResultBinding
import ksu.katara.healthymealplanner.foundation.model.EmptyResult
import ksu.katara.healthymealplanner.foundation.model.ErrorResult
import ksu.katara.healthymealplanner.foundation.model.PendingResult
import ksu.katara.healthymealplanner.foundation.model.SuccessResult
import ksu.katara.healthymealplanner.foundation.views.BaseFragment
import ksu.katara.healthymealplanner.foundation.views.BaseScreen
import ksu.katara.healthymealplanner.foundation.views.HasScreenTitle
import ksu.katara.healthymealplanner.foundation.views.screenViewModel

class DietTipDetailsFragment : BaseFragment(), HasScreenTitle {

    /**
     * This screen has 1 argument: id of chosen dietTip
     */
    class Screen(
        val dietTipId: Long
    ) : BaseScreen

    private lateinit var binding: FragmentDietTipDetailsBinding
    private lateinit var resultBinding: PartResultBinding

    override val viewModel by screenViewModel<DietTipDetailsViewModel>()

    override fun getScreenTitle(): String? = viewModel.screenTitle.value

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDietTipDetailsBinding.inflate(layoutInflater, container, false)
        resultBinding = PartResultBinding.bind(binding.root)
        initDietTipDetailsViewPager()
        return binding.root
    }

    private fun initDietTipDetailsViewPager() {
        viewModel.dietTipDetails.observe(viewLifecycleOwner) {
            hideAllInDietTipDetails()
            when (it) {
                is SuccessResult -> {
                    binding.dietTipDetailsContentContainer.visibility = View.VISIBLE
                    val dietTipDetailsViewPagerAdapter = DietTipDetailsViewPagerAdapter(it.data)
                    binding.dietTipDetailsViewPager.adapter = dietTipDetailsViewPagerAdapter
                }
                is ErrorResult -> {
                    resultBinding.errorContainer.visibility = View.VISIBLE
                }
                is PendingResult -> {
                    resultBinding.progressBar.visibility = View.VISIBLE
                }
                is EmptyResult -> {
                    resultBinding.noData.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun hideAllInDietTipDetails() {
        binding.dietTipDetailsContentContainer.visibility = View.GONE
        resultBinding.progressBar.visibility = View.GONE
        resultBinding.errorContainer.visibility = View.GONE
        resultBinding.noData.visibility = View.GONE
    }

    companion object {
        fun createArgs(screen: BaseScreen) = bundleOf(BaseScreen.ARG_SCREEN to screen)
    }
}