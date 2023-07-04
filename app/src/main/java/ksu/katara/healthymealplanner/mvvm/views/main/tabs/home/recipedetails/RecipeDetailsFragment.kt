package ksu.katara.healthymealplanner.mvvm.views.main.tabs.home.recipedetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.databinding.FragmentRecipeDetailsBinding
import ksu.katara.healthymealplanner.foundation.model.EmptyResult
import ksu.katara.healthymealplanner.foundation.model.ErrorResult
import ksu.katara.healthymealplanner.foundation.model.PendingResult
import ksu.katara.healthymealplanner.foundation.model.SuccessResult
import ksu.katara.healthymealplanner.foundation.views.BaseFragment
import ksu.katara.healthymealplanner.foundation.views.BaseScreen
import ksu.katara.healthymealplanner.foundation.views.HasScreenTitle
import ksu.katara.healthymealplanner.foundation.views.screenViewModel
import ksu.katara.healthymealplanner.mvvm.model.recipes.entities.Recipe
import ksu.katara.healthymealplanner.mvvm.model.recipes.entities.RecipeDetails
import kotlin.properties.Delegates

class RecipeDetailsFragment : BaseFragment(), HasScreenTitle {

    /**
     * This screen has 1 argument: id of chosen recipe.
     */
    class Screen(
        val recipe: Recipe
    ) : BaseScreen

    private lateinit var binding: FragmentRecipeDetailsBinding

    private lateinit var recipeTypesAdapter: TypesAdapter
    private lateinit var ingredientsAdapter: IngredientsAdapter
    private lateinit var preparationStepsAdapter: PreparationStepsAdapter

    private var isAllIngredientsSelected by Delegates.notNull<Boolean>()

    override val viewModel by screenViewModel<RecipeDetailsViewModel>()

    override fun getScreenTitle(): String? = viewModel.screenTitle.value

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeDetailsBinding.inflate(layoutInflater, container, false)
        initRecipeDetails()
        initRecipeTypes()
        initNumberOfPortions()
        initIngredients()
        initSelectAllIngredients()
        initPreparationSteps()
        return binding.root
    }

    private fun initRecipeDetails() {
        viewModel.recipeDetails.observe(viewLifecycleOwner) {
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
    }

    private fun initRecipeDetails(recipeDetails: RecipeDetails) {
        binding.recipeDetailsNameTextView.text = recipeDetails.recipe.name
        binding.recipeDetailsTimePreparationTextView.text = recipeDetails.preparationTime.toString()
        if (recipeDetails.recipe.photo.isNotBlank()) {
            Glide.with(binding.recipeDetailsPhotoImageView.context)
                .load(recipeDetails.recipe.photo)
                .circleCrop()
                .placeholder(R.drawable.ic_recipe)
                .error(R.drawable.ic_recipe)
                .into(binding.recipeDetailsPhotoImageView)
        } else {
            Glide.with(binding.recipeDetailsPhotoImageView.context)
                .clear(binding.recipeDetailsPhotoImageView)
            binding.recipeDetailsPhotoImageView.setImageResource(R.drawable.ic_recipe)
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
        viewModel.preparationSteps.observe(viewLifecycleOwner) {
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
        viewModel.isAllIngredientsSelected.observe(viewLifecycleOwner) { statusResult ->
            when (statusResult) {
                is SuccessResult -> {
                    binding.isAllIngredientsSelectedCheckBox.isChecked = statusResult.data
                    binding.isAllIngredientsSelectedCheckBox.visibility = View.VISIBLE
                    binding.recipeDetailsIsAllIngredientsSelectedProgressBar.visibility = View.INVISIBLE
                    isAllIngredientsSelected = statusResult.data
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
        binding.recipeDetailsSelectAllIngredientsContainer.setOnClickListener {
            viewModel.setAllIngredientsSelected(!isAllIngredientsSelected)
            binding.isAllIngredientsSelectedCheckBox.isChecked = !isAllIngredientsSelected
            isAllIngredientsSelected = !isAllIngredientsSelected
        }
    }

    private fun initIngredients() {
        ingredientsAdapter =
            IngredientsAdapter(viewModel)
        viewModel.ingredients.observe(viewLifecycleOwner) { statusResult ->
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
        val ingredientsItemListLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recipeDetailsIngredientsRecyclerView.layoutManager =
            ingredientsItemListLayoutManager
        binding.recipeDetailsIngredientsRecyclerView.adapter = ingredientsAdapter
        val ingredientsAnimator = binding.recipeDetailsIngredientsRecyclerView.itemAnimator
        if (ingredientsAnimator is DefaultItemAnimator) {
            ingredientsAnimator.supportsChangeAnimations = false
        }
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
        viewModel.recipeTypes.observe(viewLifecycleOwner) {
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

    companion object {
        fun createArgs(screen: BaseScreen) = bundleOf(BaseScreen.ARG_SCREEN to screen)
    }
}