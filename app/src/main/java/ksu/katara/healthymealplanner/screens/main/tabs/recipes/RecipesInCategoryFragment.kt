package ksu.katara.healthymealplanner.screens.main.tabs.recipes

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.Repositories
import ksu.katara.healthymealplanner.databinding.FragmentRecipesInCategoryBinding
import ksu.katara.healthymealplanner.tasks.EmptyResult
import ksu.katara.healthymealplanner.tasks.ErrorResult
import ksu.katara.healthymealplanner.tasks.PendingResult
import ksu.katara.healthymealplanner.tasks.SuccessResult
import ksu.katara.healthymealplanner.utils.findTopNavController
import ksu.katara.healthymealplanner.utils.viewModelCreator

class RecipesInCategoryFragment : Fragment(R.layout.fragment_recipes_in_category) {

    private lateinit var binding: FragmentRecipesInCategoryBinding

    private lateinit var recipesInCategoryAdapter: RecipesInCategoryAdapter

    private val recipesInCategoryListViewModel by viewModelCreator {
        RecipesInCategoryViewModel(
            getRecipeCategoryId(),
            Repositories.recipesRepository,
        )
    }

    private val args by navArgs<RecipesInCategoryFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRecipesInCategoryBinding.bind(view)

        initRecipesInCategoryRecycleViewSection()

        recipesInCategoryListViewModel.recipeCategory.observe(viewLifecycleOwner) {

        }
    }

    private fun initRecipesInCategoryRecycleViewSection() {
        recipesInCategoryAdapter = RecipesInCategoryAdapter(recipesInCategoryListViewModel)

        recipesInCategoryListViewModel.recipesInCategory.observe(viewLifecycleOwner) {
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

        recipesInCategoryListViewModel.actionShowDetails.observe(viewLifecycleOwner) {
            it.getValue()?.let { recipe ->
                showRecipeDetails(recipe.id)
            }
        }

        val recipeCategoriesLayoutManager =
            GridLayoutManager(requireContext(), 2)
        binding.recipesInCategoryRecyclerView.layoutManager =
            recipeCategoriesLayoutManager
        binding.recipesInCategoryRecyclerView.adapter = recipesInCategoryAdapter
    }

    private fun hideAll() = with(binding) {
        recipesInCategoryRecyclerView.visibility = View.GONE
        recipesInCategoryProgressBar.visibility = View.GONE
        recipeInCategoryTryAgainContainer.visibility = View.GONE
        noRecipesInCategoryTextView.visibility = View.GONE
    }

    private fun showRecipeDetails(recipeId: Long) {
        val direction =
            RecipesInCategoryFragmentDirections.actionRecipesInCategoryFragmentToRecipeFragment(recipeId)
        findTopNavController().navigate(direction)
    }

    private fun getRecipeCategoryId(): Long = args.recipeCategoryId
}