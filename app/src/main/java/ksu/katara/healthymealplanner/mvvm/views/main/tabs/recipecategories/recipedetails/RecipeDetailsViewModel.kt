package ksu.katara.healthymealplanner.mvvm.views.main.tabs.recipecategories.recipedetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.foundation.model.EmptyResult
import ksu.katara.healthymealplanner.foundation.model.ErrorResult
import ksu.katara.healthymealplanner.foundation.model.PendingResult
import ksu.katara.healthymealplanner.foundation.model.StatusResult
import ksu.katara.healthymealplanner.foundation.model.SuccessResult
import ksu.katara.healthymealplanner.foundation.navigator.Navigator
import ksu.katara.healthymealplanner.foundation.uiactions.UiActions
import ksu.katara.healthymealplanner.foundation.views.BaseViewModel
import ksu.katara.healthymealplanner.foundation.views.LiveResult
import ksu.katara.healthymealplanner.foundation.views.MutableLiveResult
import ksu.katara.healthymealplanner.mvvm.model.recipes.RecipeIngredientsListener
import ksu.katara.healthymealplanner.mvvm.model.recipes.RecipesRepository
import ksu.katara.healthymealplanner.mvvm.model.recipes.entities.RecipeDetails
import ksu.katara.healthymealplanner.mvvm.model.recipes.entities.RecipeIngredient
import ksu.katara.healthymealplanner.mvvm.model.recipes.entities.RecipePreparationStep
import ksu.katara.healthymealplanner.mvvm.model.shoppinglist.ShoppingListRepository
import ksu.katara.healthymealplanner.mvvm.views.main.tabs.recipecategories.recipedetails.RecipeDetailsFragment.Screen

data class IngredientsItem(
    val ingredient: RecipeIngredient,
    val isInProgress: Boolean,
)

class RecipeDetailsViewModel(
    screen: Screen,
    private val navigator: Navigator,
    private val uiActions: UiActions,
    private val recipesRepository: RecipesRepository,
    private val shoppingListRepository: ShoppingListRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel(), IngredientSelectedActionListener {

    private val _recipeDetails = MutableLiveResult<RecipeDetails>()
    val recipeDetails: LiveResult<RecipeDetails> = _recipeDetails

    private val _recipeTypes = MutableLiveResult<List<String>>()
    val recipeTypes: LiveResult<List<String>> = _recipeTypes

    private val _ingredients = MutableLiveResult<List<IngredientsItem>>()
    val ingredients: LiveResult<List<IngredientsItem>> = _ingredients

    private val _isAllIngredientsSelected = MutableLiveResult<Boolean>()
    val isAllIngredientsSelected: LiveResult<Boolean> = _isAllIngredientsSelected

    private val _preparationSteps = MutableLiveResult<List<RecipePreparationStep>>()
    val preparationSteps: LiveResult<List<RecipePreparationStep>> = _preparationSteps

    private val _screenTitle = MutableLiveData<String>()
    val screenTitle: LiveData<String> = _screenTitle

    private val ingredientsItemIdsInProgress = mutableSetOf<Long>()

    private var allIngredients: List<RecipeIngredient> = mutableListOf()

    private var ingredientsResult: StatusResult<List<RecipeIngredient>> = EmptyResult()
        set(value) {
            field = value
            notifyIngredientsUpdates()
        }

    private val ingredientsListener: RecipeIngredientsListener = {
        ingredientsResult = if (it.isEmpty()) {
            EmptyResult()
        } else {
            SuccessResult(it)
        }
    }

    private val recipeId = screen.recipe.id
    private val recipeName = screen.recipe.name

    init {
        _screenTitle.value = uiActions.getString(R.string.recipe_details_title, recipeName)
        recipesRepository.addIngredientListener(ingredientsListener)
        loadRecipeDetails(recipeId)
        loadRecipeTypes(recipeId)
        loadIngredients(recipeId)
        loadPreparationSteps(recipeId)
    }

    private fun loadRecipeDetails(recipeId: Long) = into(_recipeDetails) {
        recipesRepository.loadRecipeDetails(recipeId)
    }

    private fun loadRecipeTypes(recipeId: Long) = into(_recipeTypes) {
        recipesRepository.loadRecipeTypes(recipeId)
    }

    private fun loadIngredients(recipeId: Long) {
        ingredientsResult = PendingResult()
        viewModelScope.launch {
            try {
                val result = recipesRepository.loadIngredients(recipeId)
                allIngredients = result
            } catch (e: Exception) {
                if (e !is CancellationException) ingredientsResult = ErrorResult(e)
            }
        }
        viewModelScope.launch {
            try {
                val result = recipesRepository.isAllIngredientsSelected(recipeId)
                if (result) {
                    _isAllIngredientsSelected.value = SuccessResult(true)
                } else {
                    _isAllIngredientsSelected.value = SuccessResult(false)
                }
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    val message = uiActions.getString(R.string.cant_make_all_ingredients_selected)
                    uiActions.toast(message)
                }
            }
        }
    }

    fun setAllIngredientsSelected(isSelected: Boolean) {
        _isAllIngredientsSelected.value = PendingResult()
        allIngredients.forEach { ingredient -> addProgressTo(ingredient) }
        viewModelScope.launch {
            try {
                recipesRepository.setAllIngredientsSelected(recipeId, isSelected)
                _isAllIngredientsSelected.value = SuccessResult(isSelected)
                allIngredients.forEach { ingredient -> removeProgressFrom(ingredient) }
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    val message = uiActions.getString(R.string.cant_make_all_ingredients_selected)
                    uiActions.toast(message)
                }
            }
        }
        viewModelScope.launch {
            try {
                shoppingListRepository.addAllIngredients(recipeId, isSelected)
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    val message = uiActions.getString(R.string.cant_add_all_ingredient_to_shopping_list)
                    uiActions.toast(message)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        recipesRepository.removeIngredientsListener(ingredientsListener)
    }

    override fun onIngredientPressed(ingredient: RecipeIngredient, isSelected: Boolean) {
        if (isInProgress(ingredient)) return
        addProgressTo(ingredient)
        viewModelScope.launch {
            try {
                recipesRepository.setIngredientSelected(recipeId, ingredient, isSelected)
                removeProgressFrom(ingredient)
            } catch (e: Exception) {
                if (e !is CancellationException) removeProgressFrom(ingredient)
            }
        }
        viewModelScope.launch {
            try {
                val result = recipesRepository.isAllIngredientsSelected(recipeId)
                if (result) {
                    _isAllIngredientsSelected.value = SuccessResult(true)
                } else {
                    _isAllIngredientsSelected.value = SuccessResult(false)
                }
            } catch (e: Exception) {
                if (e !is CancellationException) removeProgressFrom(ingredient)
            }
        }
        if (isSelected) {
            viewModelScope.launch {
                try {
                    shoppingListRepository.addIngredient(recipeId, ingredient)
                } catch (e: Exception) {
                    if (e !is CancellationException) {
                        val message = uiActions.getString(R.string.cant_add_ingredient_to_shopping_list)
                        uiActions.toast(message)
                    }
                }
            }
        } else {
            viewModelScope.launch {
                try {
                    shoppingListRepository.deleteIngredient(recipeId, ingredient)
                } catch (e: Exception) {
                    if (e !is CancellationException) {
                        val message = uiActions.getString(R.string.cant_delete_ingredient_from_shopping_list)
                        uiActions.toast(message)
                    }
                }
            }
        }
    }

    private fun addProgressTo(ingredient: RecipeIngredient) {
        ingredientsItemIdsInProgress.add(ingredient.id)
        notifyIngredientsUpdates()
    }

    private fun removeProgressFrom(ingredient: RecipeIngredient) {
        ingredientsItemIdsInProgress.remove(ingredient.id)
        notifyIngredientsUpdates()
    }

    private fun isInProgress(ingredient: RecipeIngredient): Boolean {
        return ingredientsItemIdsInProgress.contains(ingredient.id)
    }

    private fun notifyIngredientsUpdates() {
        _ingredients.postValue(ingredientsResult.resultMap { ingredientsItemList ->
            ingredientsItemList.map { ingredient -> IngredientsItem(ingredient, isInProgress(ingredient)) }
        })
    }

    private fun loadPreparationSteps(recipeId: Long) = into(_preparationSteps) {
        recipesRepository.loadPreparationSteps(recipeId)
    }

    fun tryAgain() {
        loadRecipeDetails(recipeId)
        loadRecipeTypes(recipeId)
        loadIngredients(recipeId)
        loadPreparationSteps(recipeId)
    }

    fun loadRecipeDetailsTypesTryAgain() {
        loadRecipeTypes(recipeId)
    }

    fun loadIngredientsTryAgain() {
        loadIngredients(recipeId)
    }

    fun loadPreparationStepsTryAgain() {
        loadPreparationSteps(recipeId)
    }
}

