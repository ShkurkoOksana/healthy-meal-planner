package ksu.katara.healthymealplanner.mvvm.views.main.tabs.mealplan.addrecipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import ksu.katara.healthymealplanner.databinding.FragmentAddRecipesBinding
import ksu.katara.healthymealplanner.databinding.PartResultBinding
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

class AddRecipesFragment : BaseFragment(), HasScreenTitle {

    /**
     * This screen has 2 arguments: selected date and meal type for chosen meal plan.
     */
    class Screen(
        val selectedDate: Date,
        val mealType: MealTypes
    ) : BaseScreen

    private lateinit var binding: FragmentAddRecipesBinding
    private lateinit var resultBinding: PartResultBinding

    private lateinit var addRecipesAdapter: AddRecipesAdapter

    override val viewModel by screenViewModel<AddRecipesListViewModel>()

    /**
     * Dynamic screen title
     */
    override fun getScreenTitle(): String? = viewModel.screenTitle.value

    private var filterString: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddRecipesBinding.inflate(layoutInflater, container, false)
        resultBinding = PartResultBinding.bind(binding.root)
        initSearchAddRecipes()
        initAddRecipesList()
        return binding.root
    }

    private fun initSearchAddRecipes() {
        with(binding.addRecipeSearchView) {
            isActivated = true
            onActionViewExpanded()
            isIconified = false
            clearFocus()
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }
                override fun onQueryTextChange(newText: String?): Boolean {
                    filterString = newText
                    addRecipesAdapter.filter.filter(newText)
                    return false
                }
            })
        }
    }

    private fun initAddRecipesList() {
        addRecipesAdapter =
            AddRecipesAdapter(viewModel)

        viewModel.addRecipes.observe(viewLifecycleOwner) { statusResult ->
            hideAllAddRecipesList()
            when (statusResult) {
                is SuccessResult -> {
                    binding.addRecipesListRecyclerView.visibility = View.VISIBLE
                    resultBinding.progressBar.visibility = View.INVISIBLE
                    addRecipesAdapter.addRecipesList = statusResult.data
                    addRecipesAdapter.filter.filter(filterString)
                    addRecipesAdapter.addRecipesListFilter = statusResult.data
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
        val recipesListLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.addRecipesListRecyclerView.layoutManager =
            recipesListLayoutManager
        binding.addRecipesListRecyclerView.adapter =
            addRecipesAdapter
        val addRecipesListAnimator = binding.addRecipesListRecyclerView.itemAnimator
        if (addRecipesListAnimator is DefaultItemAnimator) {
            addRecipesListAnimator.supportsChangeAnimations = false
        }
    }

    private fun hideAllAddRecipesList() {
        binding.addRecipesListRecyclerView.visibility = View.GONE
        resultBinding.errorContainer.visibility = View.GONE
        resultBinding.progressBar.visibility = View.GONE
        resultBinding.noData.visibility = View.GONE
    }

    companion object {
        fun createArgs(screen: BaseScreen) = bundleOf(BaseScreen.ARG_SCREEN to screen)
    }
}