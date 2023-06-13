package ksu.katara.healthymealplanner.mvvm.screens.main.tabs.mealplan.mealplanfordate.addrecipe

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.databinding.FragmentAddRecipesBinding
import ksu.katara.healthymealplanner.mvvm.Repositories
import ksu.katara.healthymealplanner.mvvm.tasks.EmptyResult
import ksu.katara.healthymealplanner.mvvm.tasks.ErrorResult
import ksu.katara.healthymealplanner.mvvm.tasks.PendingResult
import ksu.katara.healthymealplanner.mvvm.tasks.SuccessResult
import ksu.katara.healthymealplanner.mvvm.utils.viewModelCreator

class AddRecipeFragment : Fragment(R.layout.fragment_add_recipes) {

    private lateinit var binding: FragmentAddRecipesBinding

    private lateinit var addRecipesListAdapter: AddRecipesListAdapter

    private var filterString: String? = null

    private val args by navArgs<AddRecipeFragmentArgs>()

    private val addRecipesListViewModel by viewModelCreator {
        AddRecipesListViewModel(
            getSelectedDate(),
            getMealType(),
            Repositories.addRecipesRepository,
            Repositories.mealPlanForDateRecipesRepository,
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddRecipesBinding.bind(view)
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
                    addRecipesListAdapter.filter.filter(newText)
                    return false
                }
            })
        }
    }

    private fun initAddRecipesList() {
        addRecipesListAdapter =
            AddRecipesListAdapter(addRecipesListViewModel)

        addRecipesListViewModel.addRecipes.observe(viewLifecycleOwner) { statusResult ->
            hideAllAddRecipesList()
            when (statusResult) {
                is SuccessResult -> {
                    binding.addRecipesListRecyclerView.visibility = View.VISIBLE
                    binding.addRecipesListProgressBar.visibility = View.INVISIBLE
                    addRecipesListAdapter.addRecipesList = statusResult.data
                    addRecipesListAdapter.filter.filter(filterString)
                    addRecipesListAdapter.addRecipesListFilter = statusResult.data
                }

                is ErrorResult -> {
                    binding.recipesListTryAgainContainer.visibility = View.VISIBLE
                }

                is PendingResult -> {
                    binding.addRecipesListProgressBar.visibility = View.VISIBLE
                }

                is EmptyResult -> {
                    binding.noRecipesTextView.visibility = View.VISIBLE
                }
            }
        }
        addRecipesListViewModel.actionShowToast.observe(viewLifecycleOwner) {
            it.getValue()?.let { messageRes -> Toast.makeText(requireContext(), messageRes, Toast.LENGTH_SHORT).show() }
        }
        val recipesListLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.addRecipesListRecyclerView.layoutManager =
            recipesListLayoutManager
        binding.addRecipesListRecyclerView.adapter =
            addRecipesListAdapter
        val addRecipesListAnimator = binding.addRecipesListRecyclerView.itemAnimator
        if (addRecipesListAnimator is DefaultItemAnimator) {
            addRecipesListAnimator.supportsChangeAnimations = false
        }
    }

    private fun hideAllAddRecipesList() = with(binding) {
        addRecipesListRecyclerView.visibility = View.GONE
        addRecipesListProgressBar.visibility = View.GONE
        recipesListTryAgainContainer.visibility = View.GONE
        noRecipesTextView.visibility = View.GONE
    }

    private fun getMealType() = args.mealType

    private fun getSelectedDate() = args.selectedDate
}