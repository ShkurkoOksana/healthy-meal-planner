package ksu.katara.healthymealplanner.mvvm.presentation.main.tabs.mealplan.addrecipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ksu.katara.healthymealplanner.databinding.FragmentAddRecipesBinding
import ksu.katara.healthymealplanner.foundation.utils.viewModelCreator
import ksu.katara.healthymealplanner.foundation.views.BaseFragment
import ksu.katara.healthymealplanner.foundation.views.BaseScreen
import ksu.katara.healthymealplanner.foundation.views.HasScreenTitle
import ksu.katara.healthymealplanner.foundation.views.onTryAgain
import ksu.katara.healthymealplanner.foundation.views.renderSimpleResult
import ksu.katara.healthymealplanner.mvvm.presentation.main.tabs.home.MealTypes
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class AddRecipesFragment : BaseFragment(), HasScreenTitle {

    /**
     * This screen has 2 arguments: selected date and meal type for chosen meal plan.
     */
    class Screen(
        val selectedDate: Date,
        val mealType: MealTypes
    ) : BaseScreen

    private lateinit var binding: FragmentAddRecipesBinding

    private lateinit var addRecipesAdapter: AddRecipesAdapter

    @Inject
    lateinit var factory: AddRecipesListViewModel.Factory

    override val viewModel by viewModelCreator<AddRecipesListViewModel> {
        factory.create(requireArguments().getSerializable(BaseScreen.ARG_SCREEN) as BaseScreen)
    }

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
        initView()
        return binding.root
    }

    private fun initView() {
        initSearchAddRecipes()
        initAddRecipesList()
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

        viewModel.addRecipes.observe(viewLifecycleOwner) { result ->
            renderSimpleResult(
                root = binding.root,
                result = result,
                onSuccess = {
                    addRecipesAdapter.addRecipesList = it
                    addRecipesAdapter.filter.filter(filterString)
                    addRecipesAdapter.addRecipesListFilter = it
                }
            )
        }
        onTryAgain(binding.root) {
            viewModel.tryAgain()
        }
        val recipesListLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.addRecipesRecyclerView.layoutManager =
            recipesListLayoutManager
        binding.addRecipesRecyclerView.adapter =
            addRecipesAdapter
        val addRecipesListAnimator = binding.addRecipesRecyclerView.itemAnimator
        if (addRecipesListAnimator is DefaultItemAnimator) {
            addRecipesListAnimator.supportsChangeAnimations = false
        }
    }

    companion object {
        fun createArgs(screen: BaseScreen) = bundleOf(BaseScreen.ARG_SCREEN to screen)
    }
}