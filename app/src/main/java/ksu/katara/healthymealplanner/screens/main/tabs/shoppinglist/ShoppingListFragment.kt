package ksu.katara.healthymealplanner.screens.main.tabs.shoppinglist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.Repositories
import ksu.katara.healthymealplanner.databinding.FragmentShoppingListBinding
import ksu.katara.healthymealplanner.screens.main.tabs.TabsFragmentDirections
import ksu.katara.healthymealplanner.tasks.EmptyResult
import ksu.katara.healthymealplanner.tasks.ErrorResult
import ksu.katara.healthymealplanner.tasks.PendingResult
import ksu.katara.healthymealplanner.tasks.SuccessResult
import ksu.katara.healthymealplanner.utils.findTopNavController
import ksu.katara.healthymealplanner.utils.viewModelCreator

class ShoppingListFragment : Fragment(R.layout.fragment_shopping_list) {

    private lateinit var binding: FragmentShoppingListBinding

    private lateinit var shoppingListAdapter: ShoppingListAdapter

    private val shoppingListViewModel by viewModelCreator {
        ShoppingListViewModel(
            Repositories.shoppingListRepository,
            Repositories.recipesRepository,
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentShoppingListBinding.bind(view)

        initShoppingList()
    }

    private fun initShoppingList() {
        shoppingListAdapter = ShoppingListAdapter(requireContext(), shoppingListViewModel)

        shoppingListViewModel.shoppingList.observe(viewLifecycleOwner) {
            hideAll()
            when (it) {
                is SuccessResult -> {
                    binding.shoppingListRecyclerView.visibility = View.VISIBLE
                    shoppingListAdapter.shoppingList = it.data
                }

                is ErrorResult -> {
                    binding.noShoppingListTextView.visibility = View.VISIBLE
                }

                is PendingResult -> {
                    binding.shoppingListProgressBar.visibility = View.VISIBLE
                }

                is EmptyResult -> {
                    binding.noShoppingListTextView.visibility = View.VISIBLE
                }
            }
        }

        shoppingListViewModel.actionShowRecipeDetails.observe(viewLifecycleOwner) {
            it.getValue()?.let { shoppingListRecipe ->
                showRecipeDetails(shoppingListRecipe.recipe.id)
            }
        }

        val shoppingListLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.shoppingListRecyclerView.layoutManager = shoppingListLayoutManager
        binding.shoppingListRecyclerView.adapter = shoppingListAdapter
    }

    private fun hideAll() = with(binding) {
        shoppingListRecyclerView.visibility = View.GONE
        shoppingListProgressBar.visibility = View.GONE
        shoppingListTryAgainContainer.visibility = View.GONE
        noShoppingListTextView.visibility = View.GONE
    }

    private fun showRecipeDetails(recipeId: Long) {
        val direction = TabsFragmentDirections.actionTabsFragmentToRecipeFragment(recipeId)
        findTopNavController().navigate(direction)
    }
}