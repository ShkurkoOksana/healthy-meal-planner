package ksu.katara.healthymealplanner.screens.main.tabs.mealplan.mealplanfordate

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.Repositories
import ksu.katara.healthymealplanner.databinding.FragmentMealPlanForDateRecipesBinding
import ksu.katara.healthymealplanner.model.meal.enum.MealTypes
import ksu.katara.healthymealplanner.tasks.EmptyResult
import ksu.katara.healthymealplanner.tasks.ErrorResult
import ksu.katara.healthymealplanner.tasks.PendingResult
import ksu.katara.healthymealplanner.tasks.SuccessResult
import ksu.katara.healthymealplanner.utils.findTopNavController
import ksu.katara.healthymealplanner.utils.viewModelCreator

class MealPlanForDateRecipesFragment : Fragment(R.layout.fragment_meal_plan_for_date_recipes) {

    private lateinit var binding: FragmentMealPlanForDateRecipesBinding

    private lateinit var mealPlanForDateRecipesAdapter: MealPlanForDateRecipesAdapter

    private val args by navArgs<MealPlanForDateRecipesFragmentArgs>()

    private val mealPlanForDateRecipesViewModel by viewModelCreator {
        MealPlanDateRecipesListViewModel(
            getSelectedDate(),
            getMealType(),
            Repositories.mealPlanForDateRecipesRepository,
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMealPlanForDateRecipesBinding.bind(view)
        binding.mealPlanForDateRecipesAddRecipeButton.setOnClickListener {
            onMealPlanForDateAddButtonPressed(getSelectedDate(), getMealType())
        }
        initMealPlanForDateRecipes()
    }

    private fun onMealPlanForDateAddButtonPressed(selectedDate: String, mealType: MealTypes) {
        val direction =
            MealPlanForDateRecipesFragmentDirections.actionMealPlanForDateFragmentToAddRecipeFragment(mealType, selectedDate)
        findTopNavController().navigate(direction)
        binding.mealPlanForDateRecipesAddRecipeButton.visibility = View.INVISIBLE
        binding.mealPlanForDateRecipesProgressBarForAddButton.visibility = View.VISIBLE
    }

    private fun initMealPlanForDateRecipes() {
        mealPlanForDateRecipesAdapter =
            MealPlanForDateRecipesAdapter(mealPlanForDateRecipesViewModel)
        mealPlanForDateRecipesViewModel.mealPlanForDateRecipes.observe(viewLifecycleOwner) { statusResult ->
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
        mealPlanForDateRecipesViewModel.actionShowDetails.observe(viewLifecycleOwner) {
            it.getValue()?.let { recipe -> onMealPlanForDateRecipesItemPressed(recipe.id) }
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

    private fun onMealPlanForDateRecipesItemPressed(recipeId: Long) {
        val direction =
            MealPlanForDateRecipesFragmentDirections.actionMealPlanForDateFragmentToRecipeFragment(recipeId)

        findTopNavController().navigate(direction)
    }

    private fun hideAll() = with(binding) {
        mealPlanForDateRecipesRecyclerView.visibility = View.GONE
        mealPlanForDateRecipesProgressBar.visibility = View.GONE
        mealPlanForDateRecipesTryAgainContainer.visibility = View.GONE
        noMealPlanForDateRecipesTextView.visibility = View.GONE
        mealPlanForDateRecipesAddRecipeButton.visibility = View.GONE
        mealPlanForDateRecipesProgressBarForAddButton.visibility = View.GONE
    }

    private fun getMealType() = args.mealType

    private fun getSelectedDate(): String {
        return args.selectedDate
    }
}



