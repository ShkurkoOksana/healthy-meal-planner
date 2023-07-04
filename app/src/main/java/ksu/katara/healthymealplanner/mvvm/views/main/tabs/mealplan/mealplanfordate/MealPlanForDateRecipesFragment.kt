package ksu.katara.healthymealplanner.mvvm.views.main.tabs.mealplan.mealplanfordate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import ksu.katara.healthymealplanner.databinding.FragmentMealPlanForDateRecipesBinding
import ksu.katara.healthymealplanner.foundation.model.EmptyResult
import ksu.katara.healthymealplanner.foundation.model.ErrorResult
import ksu.katara.healthymealplanner.foundation.model.PendingResult
import ksu.katara.healthymealplanner.foundation.model.SuccessResult
import ksu.katara.healthymealplanner.foundation.views.BaseFragment
import ksu.katara.healthymealplanner.foundation.views.BaseScreen
import ksu.katara.healthymealplanner.foundation.views.HasScreenTitle
import ksu.katara.healthymealplanner.foundation.views.screenViewModel
import ksu.katara.healthymealplanner.mvvm.views.main.tabs.home.MealTypes
import java.util.Date

class MealPlanForDateRecipesFragment : BaseFragment(), HasScreenTitle {

    /**
     * This screen has 2 arguments: selected date and meal type for chosen meal plan.
     */
    class Screen(
        val selectedDate: Date,
        val mealType: MealTypes
    ) : BaseScreen

    private lateinit var binding: FragmentMealPlanForDateRecipesBinding

    private lateinit var mealPlanForDateRecipesAdapter: MealPlanForDateRecipesAdapter

    override val viewModel by screenViewModel<MealPlanForDateRecipesViewModel>()

    override fun getScreenTitle(): String? = viewModel.screenTitle.value

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMealPlanForDateRecipesBinding.inflate(layoutInflater, container, false)
        binding.mealPlanForDateRecipesAddRecipeButton.setOnClickListener {
            viewModel.onMealPlanForDateAddButtonPressed()
            binding.mealPlanForDateRecipesAddRecipeButton.visibility = View.INVISIBLE
            binding.mealPlanForDateRecipesProgressBarForAddButton.visibility = View.VISIBLE
        }
        initMealPlanForDateRecipes()
        return binding.root
    }

    private fun initMealPlanForDateRecipes() {
        mealPlanForDateRecipesAdapter =
            MealPlanForDateRecipesAdapter(viewModel)
        viewModel.mealPlanForDateRecipes.observe(viewLifecycleOwner) { statusResult ->
            hideAll()
            when (statusResult) {
                is SuccessResult -> {
                    binding.mealPlanForDateRecipesRecyclerView.visibility = View.VISIBLE
                    binding.mealPlanForDateRecipesProgressBar.visibility = View.INVISIBLE
                    binding.mealPlanForDateRecipesAddRecipeButton.visibility = View.VISIBLE
                    mealPlanForDateRecipesAdapter.mealPlanForDateRecipes = statusResult.data
                }
                is ErrorResult -> {
                    binding.mealPlanForDateRecipesTryAgainContainer.visibility = View.VISIBLE
                }
                is PendingResult -> {
                    binding.mealPlanForDateRecipesProgressBar.visibility = View.VISIBLE
                }
                is EmptyResult -> {
                    binding.noMealPlanForDateRecipesTextView.visibility = View.VISIBLE
                    binding.mealPlanForDateRecipesAddRecipeButton.visibility = View.VISIBLE
                }
            }
        }
        val mealPlanForDateRecipesLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.mealPlanForDateRecipesRecyclerView.layoutManager =
            mealPlanForDateRecipesLayoutManager
        binding.mealPlanForDateRecipesRecyclerView.adapter =
            mealPlanForDateRecipesAdapter
        val mealPlanForDateRecipesViewModelAnimator = binding.mealPlanForDateRecipesRecyclerView.itemAnimator
        if (mealPlanForDateRecipesViewModelAnimator is DefaultItemAnimator) {
            mealPlanForDateRecipesViewModelAnimator.supportsChangeAnimations = false
        }
    }

    private fun hideAll() = with(binding) {
        mealPlanForDateRecipesRecyclerView.visibility = View.GONE
        mealPlanForDateRecipesProgressBar.visibility = View.GONE
        mealPlanForDateRecipesTryAgainContainer.visibility = View.GONE
        noMealPlanForDateRecipesTextView.visibility = View.GONE
        mealPlanForDateRecipesAddRecipeButton.visibility = View.GONE
        mealPlanForDateRecipesProgressBarForAddButton.visibility = View.GONE
    }

    companion object {
        fun createArgs(screen: BaseScreen) = bundleOf(BaseScreen.ARG_SCREEN to screen)
    }
}