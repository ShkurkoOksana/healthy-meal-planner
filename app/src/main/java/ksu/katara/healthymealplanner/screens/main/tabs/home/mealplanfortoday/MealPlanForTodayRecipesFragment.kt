package ksu.katara.healthymealplanner.screens.main.tabs.home.mealplanfortoday

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.Repositories
import ksu.katara.healthymealplanner.databinding.FragmentMealPlanForTodayRecipesBinding
import ksu.katara.healthymealplanner.model.meal.enum.MealTypes
import ksu.katara.healthymealplanner.tasks.EmptyResult
import ksu.katara.healthymealplanner.tasks.ErrorResult
import ksu.katara.healthymealplanner.tasks.PendingResult
import ksu.katara.healthymealplanner.tasks.SuccessResult
import ksu.katara.healthymealplanner.utils.viewModelCreator

class MealPlanForTodayRecipesFragment : Fragment(R.layout.fragment_meal_plan_for_today_recipes) {

    private lateinit var binding: FragmentMealPlanForTodayRecipesBinding

    private lateinit var mealPlanForTodayRecipesAdapter: MealPlanForTodayRecipesAdapter

    private val args by navArgs<MealPlanForTodayRecipesFragmentArgs>()

    private val mealPlanForTodayRecipesViewModel by viewModelCreator {
        MealPlanForTodayRecipeListViewModel(
            getMealType(),
            Repositories.mealPlanForTodayRecipesRepository
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMealPlanForTodayRecipesBinding.bind(view)

        binding.mealPlanForTodayRecipesAddRecipeButton.setOnClickListener {
            onMealPlanForTodayAddButtonPressed()
        }

        initMealPlanForTodayRecipes()
    }

    private fun onMealPlanForTodayAddButtonPressed() {
        binding.mealPlanForTodayRecipesAddRecipeButton.visibility = View.INVISIBLE
        binding.mealPlanForTodayRecipesProgressBarForAddButton.visibility = View.VISIBLE

        mealPlanForTodayRecipesViewModel.onMealPlanForTodayRecipeAdd()
    }

    private fun initMealPlanForTodayRecipes() {
        mealPlanForTodayRecipesAdapter =
            MealPlanForTodayRecipesAdapter(mealPlanForTodayRecipesViewModel)

        mealPlanForTodayRecipesViewModel.mealPlanForTodayRecipes.observe(viewLifecycleOwner) { statusResult ->
            val size = mealPlanForTodayRecipesAdapter.itemCount

            hideAll()
            when (statusResult) {
                is SuccessResult -> {
                    binding.mealPlanForTodayRecipesRecyclerView.visibility = View.VISIBLE
                    binding.mealPlanForTodayRecipesProgressBar.visibility = View.INVISIBLE
                    binding.mealPlanForTodayRecipesAddRecipeButton.visibility = View.VISIBLE
                    mealPlanForTodayRecipesAdapter.mealPlanForTodayRecipes = statusResult.data
                }

                is ErrorResult -> {
                    binding.mealPlanForTodayRecipesTryAgainContainer.visibility = View.VISIBLE
                }

                is PendingResult -> {
                    binding.mealPlanForTodayRecipesProgressBar.visibility = View.VISIBLE
                }
                is EmptyResult -> {
                    binding.noMealPlanForTodayRecipes.visibility = View.VISIBLE
                }
            }
        }

        mealPlanForTodayRecipesViewModel.actionShowDetails.observe(viewLifecycleOwner) {
        }

        val mealPlanForTodayRecipesLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.mealPlanForTodayRecipesRecyclerView.layoutManager =
            mealPlanForTodayRecipesLayoutManager
        binding.mealPlanForTodayRecipesRecyclerView.adapter =
            mealPlanForTodayRecipesAdapter
    }

    private fun onMealPlanForTodayItemPressed(recipeId: Long) {
    }

    private fun hideAll() = with(binding) {
        mealPlanForTodayRecipesRecyclerView.visibility = View.GONE
        mealPlanForTodayRecipesProgressBar.visibility = View.GONE
        mealPlanForTodayRecipesTryAgainContainer.visibility = View.GONE
        noMealPlanForTodayRecipes.visibility = View.GONE

        mealPlanForTodayRecipesAddRecipeButton.visibility = View.INVISIBLE
        mealPlanForTodayRecipesProgressBarForAddButton.visibility = View.INVISIBLE
    }

    private fun getMealType() = MealTypes.valueOf(args.mealType)
}



