package ksu.katara.healthymealplanner.mvvm.views.main.tabs.recipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import ksu.katara.healthymealplanner.databinding.FragmentRecipesInCategoryBinding
import ksu.katara.healthymealplanner.foundation.model.EmptyResult
import ksu.katara.healthymealplanner.foundation.model.ErrorResult
import ksu.katara.healthymealplanner.foundation.model.PendingResult
import ksu.katara.healthymealplanner.foundation.model.SuccessResult
import ksu.katara.healthymealplanner.foundation.views.BaseFragment
import ksu.katara.healthymealplanner.foundation.views.BaseScreen
import ksu.katara.healthymealplanner.foundation.views.HasScreenTitle
import ksu.katara.healthymealplanner.foundation.views.screenViewModel
import ksu.katara.healthymealplanner.mvvm.model.recipecategories.entities.Category

class RecipesInCategoryFragment : BaseFragment(), HasScreenTitle {

    /**
     * This screen has 1 argument: selected recipeCategory.
     */
    class Screen(
        val recipeCategory: Category
    ) : BaseScreen

    private lateinit var binding: FragmentRecipesInCategoryBinding

    private lateinit var recipesInCategoryAdapter: RecipesInCategoryAdapter

    override val viewModel by screenViewModel<RecipesInCategoryViewModel>()

    override fun getScreenTitle(): String? = viewModel.screenTitle.value

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipesInCategoryBinding.inflate(layoutInflater, container, false)
        initRecipesInCategoryRecycleViewSection()
        return binding.root
    }

    private fun initRecipesInCategoryRecycleViewSection() {
        recipesInCategoryAdapter = RecipesInCategoryAdapter(viewModel)
        viewModel.recipesInCategory.observe(viewLifecycleOwner) {
            hideAll()
            when (it) {
                is SuccessResult -> {
                    binding.recipesInCategoryRecyclerView.visibility = View.VISIBLE
                    recipesInCategoryAdapter.recipesInCategory = it.data
                }
                is ErrorResult -> {
                    binding.noRecipesInCategoryTextView.visibility = View.VISIBLE
                }
                is PendingResult -> {
                    binding.recipesInCategoryProgressBar.visibility = View.VISIBLE
                }
                is EmptyResult -> {
                    binding.noRecipesInCategoryTextView.visibility = View.VISIBLE
                }
            }
        }
        val recipeCategoriesLayoutManager =
            GridLayoutManager(requireContext(), 2)
        binding.recipesInCategoryRecyclerView.layoutManager =
            recipeCategoriesLayoutManager
        binding.recipesInCategoryRecyclerView.adapter = recipesInCategoryAdapter
        val recipesInCategoryAnimator = binding.recipesInCategoryRecyclerView.itemAnimator
        if (recipesInCategoryAnimator is DefaultItemAnimator) {
            recipesInCategoryAnimator.supportsChangeAnimations = false
        }
    }

    private fun hideAll() = with(binding) {
        recipesInCategoryRecyclerView.visibility = View.GONE
        recipesInCategoryProgressBar.visibility = View.GONE
        recipeInCategoryTryAgainContainer.visibility = View.GONE
        noRecipesInCategoryTextView.visibility = View.GONE
    }

    companion object {
        fun createArgs(screen: BaseScreen) = bundleOf(BaseScreen.ARG_SCREEN to screen)
    }
}