package ksu.katara.healthymealplanner.screens.main.tabs.home.recipe

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.Repositories
import ksu.katara.healthymealplanner.databinding.FragmentRecipeBinding
import ksu.katara.healthymealplanner.screens.main.tabs.home.recipe.ingredients.IngredientsAdapter
import ksu.katara.healthymealplanner.screens.main.tabs.home.recipe.ingredients.IngredientsViewModel
import ksu.katara.healthymealplanner.screens.main.tabs.home.recipe.preparationsteps.PreparationStepsAdapter
import ksu.katara.healthymealplanner.screens.main.tabs.home.recipe.preparationsteps.RecipePreparationStepsListViewModel
import ksu.katara.healthymealplanner.screens.main.tabs.home.recipe.types.RecipeTypesListViewModel
import ksu.katara.healthymealplanner.screens.main.tabs.home.recipe.types.TypesAdapter
import ksu.katara.healthymealplanner.tasks.EmptyResult
import ksu.katara.healthymealplanner.tasks.ErrorResult
import ksu.katara.healthymealplanner.tasks.PendingResult
import ksu.katara.healthymealplanner.tasks.SuccessResult
import ksu.katara.healthymealplanner.utils.viewModelCreator

class RecipeFragment : Fragment(R.layout.fragment_recipe) {

    private lateinit var binding: FragmentRecipeBinding

    private lateinit var recipeTypesAdapter: TypesAdapter
    private lateinit var ingredientsAdapter: IngredientsAdapter
    private lateinit var preparationStepsAdapter: PreparationStepsAdapter

    private val recipeViewModel by viewModelCreator {
        RecipeViewModel(
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

    private val args by navArgs<RecipeFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recipeViewModel.loadRecipe()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRecipeBinding.bind(view)

        initRecipe()

        initRecipeTypes()

        initNumberOfPortions()

        initIngredients()

        initSelectAllIngredients()

        initPreparationSteps()
    }

    private fun initRecipe() {
        recipeViewModel.recipeState.observe(viewLifecycleOwner) {
            binding.recipeContentContainer.visibility = if (it.showContent) {
                val recipeFullDetails = (it.recipeFullDetailsResult as SuccessResult).data
                binding.recipeNameTextView.text = recipeFullDetails.recipe.name
                if (recipeFullDetails.recipe.photo.isNotBlank()) {
                    Glide.with(this)
                        .load(recipeFullDetails.recipe.photo)
                        .circleCrop()
                        .into(binding.recipePhotoImageView)
                } else {
                    Glide.with(this)
                        .load(R.drawable.ic_recipe_default_photo)
                        .into(binding.recipePhotoImageView)
                }

                binding.recipeTimePreparationTextView.text = recipeFullDetails.preparationTime.toString()

                if (recipeFullDetails.recipe.photo.isNotBlank()) {
                    Glide.with(binding.recipePhotoImageView.context)
                        .load(recipeFullDetails.recipe.photo)
                        .circleCrop()
                        .placeholder(R.drawable.ic_recipe_default_photo)
                        .error(R.drawable.ic_recipe_default_photo)
                        .into(binding.recipePhotoImageView)
                } else {
                    Glide.with(binding.recipePhotoImageView.context)
                        .clear(binding.recipePhotoImageView)
                    binding.recipePhotoImageView.setImageResource(R.drawable.ic_recipe_default_photo)
                }

                binding.recipeCuisineTypeTextView.text = recipeFullDetails.cuisineType

                binding.proteinsValueTextView.text =
                    getString(R.string.protein_value, recipeFullDetails.proteins.toString())

                binding.fatsValueTextView.text =
                    getString(R.string.protein_value, recipeFullDetails.fats.toString())

                binding.carbohydratesValueTextView.text =
                    getString(R.string.protein_value, recipeFullDetails.carbohydrates.toString())

                View.VISIBLE
            } else {
                View.GONE
            }

            binding.recipeProgressBar.visibility = if (it.showProgress) View.VISIBLE else View.GONE
        }
    }

    private fun initPreparationSteps() {
        preparationStepsAdapter = PreparationStepsAdapter()

        recipePreparationStepsListViewModel.preparationSteps.observe(viewLifecycleOwner) {
            hideAllInPreparationSteps()
            when (it) {
                is SuccessResult -> {
                    binding.preparationStepsRecyclerView.visibility = View.VISIBLE
                    preparationStepsAdapter.preparationSteps = it.data
                }

                is ErrorResult -> {
                    binding.preparationStepsTryAgainContainer.visibility = View.VISIBLE
                }

                is PendingResult -> {
                    binding.preparationStepsProgressBar.visibility = View.VISIBLE
                }

                is EmptyResult -> {
                    binding.noPreparationStepsTextView.visibility = View.VISIBLE
                }
            }
        }

        val preparationStepsLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.preparationStepsRecyclerView.layoutManager = preparationStepsLayoutManager
        binding.preparationStepsRecyclerView.adapter = preparationStepsAdapter
    }

    private fun hideAllInPreparationSteps() = with(binding) {
        preparationStepsRecyclerView.visibility = View.GONE
        preparationStepsProgressBar.visibility = View.GONE
        preparationStepsTryAgainContainer.visibility = View.GONE
        noPreparationStepsTextView.visibility = View.GONE
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
                    binding.isAllIngredientsSelectedProgressBar.visibility = View.INVISIBLE
                }

                is ErrorResult -> {

                }

                is PendingResult -> {
                    binding.isAllIngredientsSelectedCheckBox.visibility = View.INVISIBLE
                    binding.isAllIngredientsSelectedProgressBar.visibility = View.VISIBLE
                }

                is EmptyResult -> {
                }
            }
        }
    }

    private fun initIngredients() {
        ingredientsAdapter =
            IngredientsAdapter(ingredientsViewModel)

        ingredientsViewModel.ingredients.observe(viewLifecycleOwner) { statusResult ->
            hideAllIngredients()
            when (statusResult) {
                is SuccessResult -> {
                    binding.ingredientsRecyclerView.visibility = View.VISIBLE
                    binding.ingredientsProgressBar.visibility = View.INVISIBLE
                    ingredientsAdapter.ingredients = statusResult.data
                }

                is ErrorResult -> {
                    binding.ingredientsTryAgainContainer.visibility = View.VISIBLE
                }

                is PendingResult -> {
                    binding.ingredientsProgressBar.visibility = View.VISIBLE
                }

                is EmptyResult -> {
                    binding.noIngredientsTextView.visibility = View.VISIBLE
                }
            }
        }

        val ingredientsItemListLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.ingredientsRecyclerView.layoutManager =
            ingredientsItemListLayoutManager
        binding.ingredientsRecyclerView.adapter = ingredientsAdapter
    }

    private fun hideAllIngredients() = with(binding) {
        ingredientsRecyclerView.visibility = View.GONE
        ingredientsProgressBar.visibility = View.GONE
        ingredientsTryAgainContainer.visibility = View.GONE
        noIngredientsTextView.visibility = View.GONE
    }

    private fun initNumberOfPortions() {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.number_of_portions,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.numberOfPortionsSpinner.adapter = it
        }
    }

    private fun initRecipeTypes() {
        recipeTypesAdapter = TypesAdapter()

        recipeTypesListViewModel.recipeTypes.observe(viewLifecycleOwner) {
            hideAllRecipeTypes()
            when (it) {
                is SuccessResult -> {
                    binding.recipeTypesRecyclerView.visibility = View.VISIBLE
                    recipeTypesAdapter.recipeTypes = it.data
                }

                is ErrorResult -> {
                    binding.recipeTypesTryAgainContainer.visibility = View.VISIBLE
                }

                is PendingResult -> {
                    binding.recipeTypesProgressBar.visibility = View.VISIBLE
                }

                is EmptyResult -> {
                    binding.noRecipeTypesTextView.visibility = View.VISIBLE
                }
            }
        }

        val recipeTypesLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recipeTypesRecyclerView.layoutManager = recipeTypesLayoutManager
        binding.recipeTypesRecyclerView.adapter = recipeTypesAdapter
    }

    private fun hideAllRecipeTypes() = with(binding) {
        recipeTypesRecyclerView.visibility = View.GONE
        recipeTypesProgressBar.visibility = View.GONE
        recipeTypesTryAgainContainer.visibility = View.GONE
        noRecipeTypesTextView.visibility = View.GONE
    }

    private fun getRecipeId(): Long {
        return args.recipeId
    }
}