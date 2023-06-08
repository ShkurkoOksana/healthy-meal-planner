package ksu.katara.healthymealplanner.screens.main.tabs.home.recipedetails

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.Repositories
import ksu.katara.healthymealplanner.databinding.FragmentRecipeDetailsBinding
import ksu.katara.healthymealplanner.model.recipes.entities.RecipeDetails
import ksu.katara.healthymealplanner.screens.main.tabs.home.recipedetails.ingredients.IngredientsAdapter
import ksu.katara.healthymealplanner.screens.main.tabs.home.recipedetails.ingredients.IngredientsViewModel
import ksu.katara.healthymealplanner.screens.main.tabs.home.recipedetails.preparationsteps.PreparationStepsAdapter
import ksu.katara.healthymealplanner.screens.main.tabs.home.recipedetails.preparationsteps.RecipePreparationStepsListViewModel
import ksu.katara.healthymealplanner.screens.main.tabs.home.recipedetails.types.RecipeTypesListViewModel
import ksu.katara.healthymealplanner.screens.main.tabs.home.recipedetails.types.TypesAdapter
import ksu.katara.healthymealplanner.tasks.EmptyResult
import ksu.katara.healthymealplanner.tasks.ErrorResult
import ksu.katara.healthymealplanner.tasks.PendingResult
import ksu.katara.healthymealplanner.tasks.SuccessResult
import ksu.katara.healthymealplanner.utils.viewModelCreator

class RecipeDetailsFragment : Fragment(R.layout.fragment_recipe_details) {

    private lateinit var binding: FragmentRecipeDetailsBinding

    private lateinit var recipeTypesAdapter: TypesAdapter
    private lateinit var ingredientsAdapter: IngredientsAdapter
    private lateinit var preparationStepsAdapter: PreparationStepsAdapter

    private val recipeDetailsViewModel by viewModelCreator {
        RecipeDetailsViewModel(
            getRecipeId(),
            Repositories.recipesRepository
        )
    }

    private val recipePreparationStepsListViewModel by viewModelCreator {
        RecipePreparationStepsListViewModel(
            getRecipeId(),
            Repositories.recipesRepository
        )
    }

    private val recipeTypesListViewModel by viewModelCreator {
        RecipeTypesListViewModel(
            getRecipeId(),
            Repositories.recipesRepository
        )
    }

    private val ingredientsViewModel by viewModelCreator {
        IngredientsViewModel(
            getRecipeId(),
            Repositories.recipesRepository,
            Repositories.shoppingListRepository,
        )
    }

    private val args by navArgs<RecipeDetailsFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRecipeDetailsBinding.bind(view)
        initRecipeDetails()
        initRecipeTypes()
        initNumberOfPortions()
        initIngredients()
        initSelectAllIngredients()
        initPreparationSteps()
    }

    private fun initRecipeDetails() {
        recipeDetailsViewModel.recipeDetails.observe(viewLifecycleOwner) {
            hideAllInRecipeDetails()
            when (it) {
                is SuccessResult -> {
                    binding.recipeDetailsContentContainer.visibility = View.VISIBLE
                    initRecipeDetails(it.data)
                }

                is ErrorResult -> {
                    binding.recipeDetailsTryAgainContainer.visibility = View.VISIBLE
                }

                is PendingResult -> {
                    binding.recipeDetailsProgressBar.visibility = View.VISIBLE
                }

                is EmptyResult -> {
                    binding.noRecipeDetailsTextView.visibility = View.VISIBLE
                }
            }
        }
        recipeDetailsViewModel.actionShowToast.observe(viewLifecycleOwner) {
            it.getValue()?.let { messageRes -> Toast.makeText(requireContext(), messageRes, Toast.LENGTH_SHORT).show() }
        }
    }

    private fun initRecipeDetails(recipeDetails: RecipeDetails) {
        binding.recipeDetailsNameTextView.text = recipeDetails.recipe.name
        binding.recipeDetailsTimePreparationTextView.text = recipeDetails.preparationTime.toString()
        if (recipeDetails.recipe.photo.isNotBlank()) {
            Glide.with(binding.recipeDetailsPhotoImageView.context)
                .load(recipeDetails.recipe.photo)
                .circleCrop()
                .placeholder(R.drawable.ic_recipe_default_photo)
                .error(R.drawable.ic_recipe_default_photo)
                .into(binding.recipeDetailsPhotoImageView)
        } else {
            Glide.with(binding.recipeDetailsPhotoImageView.context)
                .clear(binding.recipeDetailsPhotoImageView)
            binding.recipeDetailsPhotoImageView.setImageResource(R.drawable.ic_recipe_default_photo)
        }
        binding.recipeDetailsCuisineTypeTextView.text = recipeDetails.cuisineType
        binding.recipeDetailsProteinsValueTextView.text =
            getString(R.string.protein_value, recipeDetails.proteins.toString())
        binding.recipeDetailsFatsValueTextView.text =
            getString(R.string.protein_value, recipeDetails.fats.toString())

        binding.recipeDetailsCarbohydratesValueTextView.text =
            getString(R.string.protein_value, recipeDetails.carbohydrates.toString())
    }

    private fun hideAllInRecipeDetails() = with(binding) {
        recipeDetailsContentContainer.visibility = View.GONE
        recipeDetailsProgressBar.visibility = View.GONE
        recipeDetailsTryAgainContainer.visibility = View.GONE
        noRecipeDetailsTextView.visibility = View.GONE
    }

    private fun initPreparationSteps() {
        preparationStepsAdapter = PreparationStepsAdapter()
        recipePreparationStepsListViewModel.preparationSteps.observe(viewLifecycleOwner) {
            hideAllInPreparationSteps()
            when (it) {
                is SuccessResult -> {
                    binding.recipeDetailsPreparationStepsRecyclerView.visibility = View.VISIBLE
                    preparationStepsAdapter.preparationSteps = it.data
                }

                is ErrorResult -> {
                    binding.recipeDetailsPreparationStepsTryAgainContainer.visibility = View.VISIBLE
                }

                is PendingResult -> {
                    binding.recipeDetailsPreparationStepsProgressBar.visibility = View.VISIBLE
                }

                is EmptyResult -> {
                    binding.noRecipeDetailsPreparationStepsTextView.visibility = View.VISIBLE
                }
            }
        }
        recipePreparationStepsListViewModel.actionShowToast.observe(viewLifecycleOwner) {
            it.getValue()?.let { messageRes -> Toast.makeText(requireContext(), messageRes, Toast.LENGTH_SHORT).show() }
        }
        val preparationStepsLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recipeDetailsPreparationStepsRecyclerView.layoutManager = preparationStepsLayoutManager
        binding.recipeDetailsPreparationStepsRecyclerView.adapter = preparationStepsAdapter
    }

    private fun hideAllInPreparationSteps() = with(binding) {
        recipeDetailsPreparationStepsRecyclerView.visibility = View.GONE
        recipeDetailsPreparationStepsProgressBar.visibility = View.GONE
        recipeDetailsPreparationStepsTryAgainContainer.visibility = View.GONE
        noRecipeDetailsPreparationStepsTextView.visibility = View.GONE
    }

    private fun initSelectAllIngredients() {
        binding.isAllIngredientsSelectedCheckBox.setOnCheckedChangeListener { _, isSelected ->
            ingredientsViewModel.setAllIngredientsSelected(isSelected)
            binding.isAllIngredientsSelectedCheckBox.isChecked = isSelected
        }
        ingredientsViewModel.isAllIngredientsSelected.observe(viewLifecycleOwner) { statusResult ->
            when (statusResult) {
                is SuccessResult -> {
                    binding.isAllIngredientsSelectedCheckBox.isChecked = statusResult.data
                    binding.isAllIngredientsSelectedCheckBox.visibility = View.VISIBLE
                    binding.recipeDetailsIsAllIngredientsSelectedProgressBar.visibility = View.INVISIBLE
                }

                is ErrorResult -> {
                }

                is PendingResult -> {
                    binding.isAllIngredientsSelectedCheckBox.visibility = View.INVISIBLE
                    binding.recipeDetailsIsAllIngredientsSelectedProgressBar.visibility = View.VISIBLE
                }

                is EmptyResult -> {
                }
            }
        }
        ingredientsViewModel.actionShowToast.observe(viewLifecycleOwner) {
            it.getValue()?.let { messageRes -> Toast.makeText(requireContext(), messageRes, Toast.LENGTH_SHORT).show() }
        }
    }

    private fun initIngredients() {
        ingredientsAdapter =
            IngredientsAdapter(ingredientsViewModel)
        ingredientsViewModel.ingredients.observe(viewLifecycleOwner) { statusResult ->
            hideAllIngredients()
            when (statusResult) {
                is SuccessResult -> {
                    binding.recipeDetailsIngredientsRecyclerView.visibility = View.VISIBLE
                    binding.recipeDetailsIngredientsProgressBar.visibility = View.INVISIBLE
                    ingredientsAdapter.ingredients = statusResult.data
                }

                is ErrorResult -> {
                    binding.recipeDetailsIngredientsTryAgainContainer.visibility = View.VISIBLE
                }

                is PendingResult -> {
                    binding.recipeDetailsIngredientsProgressBar.visibility = View.VISIBLE
                }

                is EmptyResult -> {
                    binding.noRecipeDetailsIngredientsTextView.visibility = View.VISIBLE
                }
            }
        }
        ingredientsViewModel.actionShowToast.observe(viewLifecycleOwner) {
            it.getValue()?.let { messageRes -> Toast.makeText(requireContext(), messageRes, Toast.LENGTH_SHORT).show() }
        }
        val ingredientsItemListLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recipeDetailsIngredientsRecyclerView.layoutManager =
            ingredientsItemListLayoutManager
        binding.recipeDetailsIngredientsRecyclerView.adapter = ingredientsAdapter
    }

    private fun hideAllIngredients() = with(binding) {
        recipeDetailsIngredientsRecyclerView.visibility = View.GONE
        recipeDetailsIngredientsProgressBar.visibility = View.GONE
        recipeDetailsIngredientsTryAgainContainer.visibility = View.GONE
        noRecipeDetailsIngredientsTextView.visibility = View.GONE
    }

    private fun initNumberOfPortions() {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.number_of_portions,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.recipeDetailsNumberOfPortionsSpinner.adapter = it
        }
    }

    private fun initRecipeTypes() {
        recipeTypesAdapter = TypesAdapter()
        recipeTypesListViewModel.recipeTypes.observe(viewLifecycleOwner) {
            hideAllRecipeTypes()
            when (it) {
                is SuccessResult -> {
                    binding.recipeDetailsTypesRecyclerView.visibility = View.VISIBLE
                    recipeTypesAdapter.recipeTypes = it.data
                }

                is ErrorResult -> {
                    binding.recipeDetailsTypesTryAgainContainer.visibility = View.VISIBLE
                }

                is PendingResult -> {
                    binding.recipeDetailsTypesProgressBar.visibility = View.VISIBLE
                }

                is EmptyResult -> {
                    binding.noRecipeDetailsTypesTextView.visibility = View.VISIBLE
                }
            }
        }

        recipeTypesListViewModel.actionShowToast.observe(viewLifecycleOwner) {
            it.getValue()?.let { messageRes -> Toast.makeText(requireContext(), messageRes, Toast.LENGTH_SHORT).show() }
        }
        val recipeTypesLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recipeDetailsTypesRecyclerView.layoutManager = recipeTypesLayoutManager
        binding.recipeDetailsTypesRecyclerView.adapter = recipeTypesAdapter
    }

    private fun hideAllRecipeTypes() = with(binding) {
        recipeDetailsTypesRecyclerView.visibility = View.GONE
        recipeDetailsTypesProgressBar.visibility = View.GONE
        recipeDetailsTypesTryAgainContainer.visibility = View.GONE
        noRecipeDetailsTypesTextView.visibility = View.GONE
    }

    private fun getRecipeId(): Long {
        return args.recipeId
    }
}