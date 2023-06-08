package ksu.katara.healthymealplanner.screens.main.tabs.home.diettips

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import ksu.katara.healthymealplanner.Repositories
import ksu.katara.healthymealplanner.databinding.FragmentDietTipDetailsBinding
import ksu.katara.healthymealplanner.tasks.EmptyResult
import ksu.katara.healthymealplanner.tasks.ErrorResult
import ksu.katara.healthymealplanner.tasks.PendingResult
import ksu.katara.healthymealplanner.tasks.SuccessResult
import ksu.katara.healthymealplanner.utils.viewModelCreator

class DietTipDetailsFragment : Fragment() {

    private lateinit var binding: FragmentDietTipDetailsBinding

    private val dietTipDetailsViewModel by viewModelCreator {
        DietTipDetailsViewModel(
            getDietTipArgument(),
            Repositories.dietTipsRepository,
        )
    }

    private val args by navArgs<DietTipDetailsFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDietTipDetailsBinding.inflate(layoutInflater, container, false)
        initDietTipDetailsViewPager()
        return binding.root
    }

    private fun initDietTipDetailsViewPager() {
        dietTipDetailsViewModel.dietTipDetails.observe(viewLifecycleOwner) {
            hideAllInDietTipDetails()
            when (it) {
                is SuccessResult -> {
                    binding.dietTipDetailsContentContainer.visibility = View.VISIBLE
                    val dietTipDetailsViewPagerAdapter = DietTipDetailsViewPagerAdapter(it.data)
                    binding.dietTipDetailsViewPager.adapter = dietTipDetailsViewPagerAdapter
                }

                is ErrorResult -> {
                    binding.dietTipDetailsTryAgainContainer.visibility = View.VISIBLE
                }

                is PendingResult -> {
                    binding.dietTipDetailsProgressBar.visibility = View.VISIBLE
                }

                is EmptyResult -> {
                    binding.noDietTipDetailsTextView.visibility = View.VISIBLE
                }
            }
        }
        dietTipDetailsViewModel.actionShowToast.observe(viewLifecycleOwner) {
            it.getValue()?.let { messageRes -> Toast.makeText(requireContext(), messageRes, Toast.LENGTH_SHORT).show() }
        }
    }

    private fun hideAllInDietTipDetails() = with(binding) {
        dietTipDetailsContentContainer.visibility = View.GONE
        dietTipDetailsProgressBar.visibility = View.GONE
        dietTipDetailsTryAgainContainer.visibility = View.GONE
        noDietTipDetailsTextView.visibility = View.GONE
    }

    private fun getDietTipArgument(): Long = args.dietTipId
}