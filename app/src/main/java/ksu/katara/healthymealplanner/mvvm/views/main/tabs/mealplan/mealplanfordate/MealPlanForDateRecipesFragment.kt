package ksu.katara.healthymealplanner.mvvm.views.main.tabs.mealplan.mealplanfordate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ksu.katara.healthymealplanner.databinding.FragmentMealPlanForDateRecipesBinding
import ksu.katara.healthymealplanner.foundation.utils.viewModelCreator
import ksu.katara.healthymealplanner.foundation.views.BaseFragment
import ksu.katara.healthymealplanner.foundation.views.BaseScreen
import ksu.katara.healthymealplanner.foundation.views.FragmentsHolder
import ksu.katara.healthymealplanner.foundation.views.HasScreenTitle
import ksu.katara.healthymealplanner.foundation.views.onTryAgain
import ksu.katara.healthymealplanner.foundation.views.renderSimpleResult
import ksu.katara.healthymealplanner.mvvm.views.main.tabs.home.MealTypes
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
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

    @Inject
    lateinit var factory: MealPlanForDateRecipesViewModel.Factory

    override val viewModel by viewModelCreator<MealPlanForDateRecipesViewModel> {
        factory.create(
            requireArguments().getSerializable(BaseScreen.ARG_SCREEN) as BaseScreen,
            (requireActivity() as FragmentsHolder).getActivityScopeViewModel().navigator
        )
    }

    override fun getScreenTitle(): String? = viewModel.screenTitle.value

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMealPlanForDateRecipesBinding.inflate(layoutInflater, container, false)
        binding.mealPlanForDateAddRecipeButton.setOnClickListener {
            viewModel.onMealPlanForDateAddButtonPressed()
            binding.mealPlanForDateAddRecipeButton.visibility = View.INVISIBLE
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
                    binding.mealPlanForDateAddRecipeButton.visibility = View.VISIBLE
                },
                onSuccess = {
                    mealPlanForDateRecipesAdapter.mealPlanForDateRecipes = it
                }
            )
        }
        onTryAgain(binding.root) {
            viewModel.loadAgain()
        }
        val mealPlanForDateRecipesLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.mealPlanForDateRecyclerView.layoutManager =
            mealPlanForDateRecipesLayoutManager
        binding.mealPlanForDateRecyclerView.adapter =
            mealPlanForDateRecipesAdapter
        val mealPlanForDateRecipesViewModelAnimator =
            binding.mealPlanForDateRecyclerView.itemAnimator
        if (mealPlanForDateRecipesViewModelAnimator is DefaultItemAnimator) {
            mealPlanForDateRecipesViewModelAnimator.supportsChangeAnimations = false
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadAgain()
    }

    companion object {
        fun createArgs(screen: BaseScreen) = bundleOf(BaseScreen.ARG_SCREEN to screen)
    }
}