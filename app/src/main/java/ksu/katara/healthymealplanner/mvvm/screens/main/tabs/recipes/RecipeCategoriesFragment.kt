package ksu.katara.healthymealplanner.mvvm.screens.main.tabs.recipes

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.databinding.FragmentRecipeCategoriesBinding
import ksu.katara.healthymealplanner.mvvm.Repositories
import ksu.katara.healthymealplanner.mvvm.screens.main.tabs.TabsFragmentDirections
import ksu.katara.healthymealplanner.mvvm.tasks.EmptyResult
import ksu.katara.healthymealplanner.mvvm.tasks.ErrorResult
import ksu.katara.healthymealplanner.mvvm.tasks.PendingResult
import ksu.katara.healthymealplanner.mvvm.tasks.SuccessResult
import ksu.katara.healthymealplanner.mvvm.utils.findTopNavController
import ksu.katara.healthymealplanner.mvvm.utils.viewModelCreator

class RecipeCategoriesFragment : Fragment(R.layout.fragment_recipe_categories) {

    private lateinit var binding: FragmentRecipeCategoriesBinding
    private lateinit var recipeCategoriesAdapter: RecipeCategoriesAdapter

    private val recipeCategoriesViewModel by viewModelCreator { RecipeCategoriesViewModel(Repositories.recipeCategoriesRepository) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRecipeCategoriesBinding.bind(view)
        initRecipeCategories()
    }

    private fun initRecipeCategories() {
        recipeCategoriesAdapter = RecipeCategoriesAdapter(recipeCategoriesViewModel)
        recipeCategoriesViewModel.recipeCategories.observe(viewLifecycleOwner) {
            hideAll()
            when (it) {
                is SuccessResult -> {
                    binding.recipeCategoriesRecyclerView.visibility = View.VISIBLE
                    recipeCategoriesAdapter.recipeCategories = it.data
                }

                is ErrorResult -> {
                    binding.noRecipeCategoriesTextView.visibility = View.VISIBLE
                }

                is PendingResult -> {
                    binding.recipesRecipeCategoriesProgressBar.visibility = View.VISIBLE
                }

                is EmptyResult -> {
                    binding.noRecipeCategoriesTextView.visibility = View.VISIBLE
                }
            }
        }
        recipeCategoriesViewModel.actionShowDetails.observe(viewLifecycleOwner) {
            it.getValue()?.let { recipeCategory -> onRecipeCategoryPressed(recipeCategory.id) }
        }
        val recipeCategoriesLayoutManager =
            GridLayoutManager(requireContext(), 2)
        binding.recipeCategoriesRecyclerView.layoutManager = recipeCategoriesLayoutManager
        binding.recipeCategoriesRecyclerView.adapter = recipeCategoriesAdapter
        val recipeCategoriesAnimator = binding.recipeCategoriesRecyclerView.itemAnimator
        if (recipeCategoriesAnimator is DefaultItemAnimator) {
            recipeCategoriesAnimator.supportsChangeAnimations = false
        }
    }

    private fun hideAll() = with(binding) {
        recipeCategoriesRecyclerView.visibility = View.GONE
        recipesRecipeCategoriesProgressBar.visibility = View.GONE
        noRecipeCategoriesTextView.visibility = View.GONE
    }

    private fun onRecipeCategoryPressed(recipeCategoryId: Long) {
        val direction = TabsFragmentDirections.actionTabsFragmentToRecipesInCategoryFragment(recipeCategoryId)
        findTopNavController().navigate(direction)
    }
}