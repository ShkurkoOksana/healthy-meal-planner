package ksu.katara.healthymealplanner.screens.main.tabs.home.diettips

import android.os.Bundle
import android.util.Log
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

    private lateinit var foundationOfHealthyLifestyleDietTipsAdapter: DietTipsAdapter
    private lateinit var nutritionRiskFactorInDevelopmentOfDiseaseDietTipsAdapter: DietTipsAdapter
    private lateinit var restorationOfDigestiveTractDietTipsAdapter: DietTipsAdapter

    private val dietTipsViewModel by viewModelCreator { DietTipsViewModel(Repositories.dietTipsRepository) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDietTipDetailsMoreBinding.bind(view)

        initView()
    }

    private fun initView() {
        initDietTipsRecycleViewSections()
    }

    private fun initDietTipsRecycleViewSections() {
        foundationOfHealthyLifestyleDietTipsAdapter = DietTipsAdapter(dietTipsViewModel)
        nutritionRiskFactorInDevelopmentOfDiseaseDietTipsAdapter = DietTipsAdapter(dietTipsViewModel)
        restorationOfDigestiveTractDietTipsAdapter = DietTipsAdapter(dietTipsViewModel)

        dietTipsViewModel.dietTips.observe(viewLifecycleOwner) {
            hideAll()
            when (it) {
                is SuccessResult -> {
                    val foundationOfHealthyLifestyleDietTips = mutableListOf<DietTipsListItem>()
                    val nutritionRiskFactorInDevelopmentOfDiseaseDietTips = mutableListOf<DietTipsListItem>()
                    val restorationOfDigestiveTractDietTips = mutableListOf<DietTipsListItem>()

                    for (dietTipListItem in it.data) {
                        when (dietTipListItem.dietTip.chapter.name) {
                            getString(R.string.foundation_of_healthy_lifestyle_title) -> {
                                foundationOfHealthyLifestyleDietTips.add(dietTipListItem)
                            }
                            getString(R.string.nutrition_risk_factor_in_development_of_disease_title) -> {
                                nutritionRiskFactorInDevelopmentOfDiseaseDietTips.add(dietTipListItem)
                            }
                            getString(R.string.restoration_of_digestive_tract_title) -> {
                                restorationOfDigestiveTractDietTips.add(dietTipListItem)
                            }
                        }
                    }
                    foundationOfHealthyLifestyleDietTipsAdapter.dietTips = foundationOfHealthyLifestyleDietTips
                    nutritionRiskFactorInDevelopmentOfDiseaseDietTipsAdapter.dietTips = nutritionRiskFactorInDevelopmentOfDiseaseDietTips
                    restorationOfDigestiveTractDietTipsAdapter.dietTips = restorationOfDigestiveTractDietTips

                    binding.foundationOfHealthyLifestyleRecyclerView.visibility = View.VISIBLE
                    binding.nutritionRiskFactorInDevelopmentOfDiseaseRecyclerView.visibility = View.VISIBLE
                    binding.restorationOfDigestiveTractRecyclerView.visibility = View.VISIBLE

                }

                is ErrorResult -> {
                    binding.foundationOfHealthyLifestyleTryAgainContainer.visibility = View.VISIBLE
                    binding.nutritionRiskFactorInDevelopmentOfDiseaseTryAgainContainer.visibility = View.VISIBLE
                    binding.restorationOfDigestiveTractTryAgainContainer.visibility = View.VISIBLE
                }

                is PendingResult -> {
                    binding.foundationOfHealthyLifestyleProgressBar.visibility = View.VISIBLE
                    binding.nutritionRiskFactorInDevelopmentOfDiseaseProgressBar.visibility = View.VISIBLE
                    binding.restorationOfDigestiveTractProgressBar.visibility = View.VISIBLE
                }
                is EmptyResult -> {
                    binding.noFoundationOfHealthyLifestyleTextView.visibility = View.VISIBLE
                    binding.noNutritionRiskFactorInDevelopmentOfDiseaseTextView.visibility = View.VISIBLE
                    binding.noRestorationOfDigestiveTractTextView.visibility = View.VISIBLE
                }
            }
        }

        dietTipsViewModel.actionShowDetails.observe(viewLifecycleOwner) {
            it.getValue()?.let { dietTip ->
                val dietTipsChapterName = dietTip.chapter.name
                onDietTipPressed(dietTipsChapterName, dietTip)
            }
        }

        val foundationOfHealthyLifestyleLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val nutritionRiskFactorInDevelopmentOfDiseaseLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val restorationOfDigestiveTractLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.foundationOfHealthyLifestyleRecyclerView.layoutManager = foundationOfHealthyLifestyleLayoutManager
        binding.foundationOfHealthyLifestyleRecyclerView.adapter = foundationOfHealthyLifestyleDietTipsAdapter
        binding.nutritionRiskFactorInDevelopmentOfDiseaseRecyclerView.layoutManager = nutritionRiskFactorInDevelopmentOfDiseaseLayoutManager
        binding.nutritionRiskFactorInDevelopmentOfDiseaseRecyclerView.adapter = nutritionRiskFactorInDevelopmentOfDiseaseDietTipsAdapter
        binding.restorationOfDigestiveTractRecyclerView.layoutManager = restorationOfDigestiveTractLayoutManager
        binding.restorationOfDigestiveTractRecyclerView.adapter = restorationOfDigestiveTractDietTipsAdapter
    }

    private fun onDietTipPressed(dietTipsChapterName: String, dietTip: DietTip) {
        val dietTipArg = dietTip.id

        val direction = DietTipDetailsMoreFragmentDirections.actionDietTipDetailsMoreFragmentToDietTipDetailsFragment(dietTipsChapterName, dietTipArg)
        findTopNavController().navigate(direction)
    }

    private fun hideAll() = with(binding) {
        foundationOfHealthyLifestyleRecyclerView.visibility = View.GONE
        foundationOfHealthyLifestyleProgressBar.visibility = View.GONE
        foundationOfHealthyLifestyleTryAgainContainer.visibility = View.GONE
        noFoundationOfHealthyLifestyleTextView.visibility = View.GONE

        nutritionRiskFactorInDevelopmentOfDiseaseRecyclerView.visibility = View.GONE
        nutritionRiskFactorInDevelopmentOfDiseaseProgressBar.visibility = View.GONE
        nutritionRiskFactorInDevelopmentOfDiseaseTryAgainContainer.visibility = View.GONE
        noNutritionRiskFactorInDevelopmentOfDiseaseTextView.visibility = View.GONE

        restorationOfDigestiveTractRecyclerView.visibility = View.GONE
        restorationOfDigestiveTractProgressBar.visibility = View.GONE
        restorationOfDigestiveTractTryAgainContainer.visibility = View.GONE
        noRestorationOfDigestiveTractTextView.visibility = View.GONE
    }
}
