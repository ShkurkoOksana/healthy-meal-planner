package ksu.katara.healthymealplanner.mvvm.views.main.tabs.mealplan.mealplanfordate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import ksu.katara.healthymealplanner.databinding.FragmentMealPlanForDateRecipesBinding
import ksu.katara.healthymealplanner.foundation.views.BaseFragment
import ksu.katara.healthymealplanner.foundation.views.BaseScreen
import ksu.katara.healthymealplanner.foundation.views.HasScreenTitle
import ksu.katara.healthymealplanner.foundation.views.renderSimpleResult
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
        }
        initView()
        return binding.root
    }

    private fun initView() {
        mealPlanForDateRecipesAdapter =
            MealPlanForDateRecipesAdapter(viewModel)
        viewModel.mealPlanForDateRecipes.observe(viewLifecycleOwner) { result ->
            renderSimpleResult(
                root = binding.root,
                result = result,
                onEmpty = {
                    binding.mealPlanForDateRecipesAddRecipeButton.visibility = View.VISIBLE
                },
                onSuccess = {
                    mealPlanForDateRecipesAdapter.mealPlanForDateRecipes = it
                }
            )
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

    companion object {
        fun createArgs(screen: BaseScreen) = bundleOf(BaseScreen.ARG_SCREEN to screen)
    }
}