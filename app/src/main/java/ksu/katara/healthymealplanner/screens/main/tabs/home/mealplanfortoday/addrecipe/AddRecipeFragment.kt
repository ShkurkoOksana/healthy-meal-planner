package ksu.katara.healthymealplanner.screens.main.tabs.home.mealplanfortoday.addrecipe

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.Repositories
import ksu.katara.healthymealplanner.databinding.FragmentAddRecipesBinding
import ksu.katara.healthymealplanner.tasks.EmptyResult
import ksu.katara.healthymealplanner.tasks.ErrorResult
import ksu.katara.healthymealplanner.tasks.PendingResult
import ksu.katara.healthymealplanner.tasks.SuccessResult
import ksu.katara.healthymealplanner.utils.viewModelCreator

class AddRecipeFragment : Fragment(R.layout.fragment_add_recipes) {

    private lateinit var binding: FragmentAddRecipesBinding

    private lateinit var addRecipesListAdapter: AddRecipesListAdapter
    private lateinit var addRecipesPhotoListAdapter: AddRecipesPhotoListAdapter

    private var filterString: String? = null

    private val args by navArgs<AddRecipeFragmentArgs>()

    private val addRecipesListViewModel by viewModelCreator {
        AddRecipesListViewModel(
            getMealType(),
            Repositories.addRecipesRepository,
            Repositories.addRecipePhotoRepository,
            Repositories.mealPlanForTodayRecipesRepository,
        )
    }

    private val addRecipesPhotoListViewModel by viewModelCreator {
        AddRecipesPhotoListViewModel(
            Repositories.addRecipesRepository,
            Repositories.addRecipePhotoRepository,
            Repositories.mealPlanForTodayRecipesRepository,
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddRecipesBinding.bind(view)

        initSearchAddRecipes()

        initAddRecipesPhotoList()

        initRecipesList()
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

    private fun initAddRecipesPhotoList() {
        addRecipesPhotoListAdapter =
            AddRecipesPhotoListAdapter(getMealType(), addRecipesPhotoListViewModel)

        addRecipesPhotoListViewModel.addRecipesPhotoList.observe(viewLifecycleOwner) { statusResult ->
            hideAllRecipesPhotoList()
            when (statusResult) {
                is SuccessResult -> {
                    binding.addRecipesPhotoRecyclerView.visibility = View.VISIBLE
                    addRecipesPhotoListAdapter.addRecipesPhotoList = statusResult.data
                }

                is ErrorResult -> {
                    binding.addRecipesPhotoListTryAgainContainer.visibility = View.VISIBLE
                }

                is PendingResult -> {
                    binding.addRecipesPhotoRecyclerView.visibility = View.INVISIBLE
                }

                is EmptyResult -> {
                    binding.addRecipesPhotoRecyclerView.visibility = View.GONE
                }
            }
        }

        val addRecipesPhotoListLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.addRecipesPhotoRecyclerView.layoutManager =
            addRecipesPhotoListLayoutManager
        binding.addRecipesPhotoRecyclerView.adapter =
            addRecipesPhotoListAdapter
    }

    private fun hideAllRecipesPhotoList() = with(binding) {
        addRecipesPhotoListTryAgainContainer.visibility = View.INVISIBLE
        noAddRecipesPhotoListTextView.visibility = View.INVISIBLE
    }

    private fun initRecipesList() {
        addRecipesListAdapter =
            AddRecipesListAdapter(getMealType(), addRecipesListViewModel)

        addRecipesListViewModel.addRecipesList.observe(viewLifecycleOwner) { statusResult ->
            hideAllRecipesList()
            when (statusResult) {
                is SuccessResult -> {
                    binding.recipesListRecyclerView.visibility = View.VISIBLE
                    binding.recipesListProgressBar.visibility = View.INVISIBLE

                    addRecipesListAdapter.addRecipesList = statusResult.data
                    addRecipesListAdapter.filter.filter(filterString)

                    addRecipesListAdapter.addRecipesListFilter = statusResult.data
                }

                is ErrorResult -> {
                    binding.recipesListTryAgainContainer.visibility = View.VISIBLE
                }

                is PendingResult -> {
                    binding.recipesListProgressBar.visibility = View.VISIBLE
                }

                is EmptyResult -> {
                    binding.noRecipesTextView.visibility = View.VISIBLE
                }
            }
        }

        val recipesListLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recipesListRecyclerView.layoutManager =
            recipesListLayoutManager
        binding.recipesListRecyclerView.adapter =
            addRecipesListAdapter
    }

    private fun hideAllRecipesList() = with(binding) {
        recipesListRecyclerView.visibility = View.GONE
        recipesListProgressBar.visibility = View.GONE
        recipesListTryAgainContainer.visibility = View.GONE
        noRecipesTextView.visibility = View.GONE
    }

    private fun getMealType() = args.mealType
}