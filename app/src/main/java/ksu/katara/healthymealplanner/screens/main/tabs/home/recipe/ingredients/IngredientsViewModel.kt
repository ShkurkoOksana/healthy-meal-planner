package ksu.katara.healthymealplanner.screens.main.tabs.home.recipe.ingredients

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ksu.katara.healthymealplanner.model.recipes.RecipeIngredientsListener
import ksu.katara.healthymealplanner.model.recipes.RecipesRepository
import ksu.katara.healthymealplanner.model.recipes.entities.RecipeIngredient
import ksu.katara.healthymealplanner.model.shoppinglist.ShoppingListRepository
import ksu.katara.healthymealplanner.screens.base.BaseViewModel
import ksu.katara.healthymealplanner.tasks.EmptyResult
import ksu.katara.healthymealplanner.tasks.ErrorResult
import ksu.katara.healthymealplanner.tasks.PendingResult
import ksu.katara.healthymealplanner.tasks.StatusResult
import ksu.katara.healthymealplanner.tasks.SuccessResult

data class IngredientsItem(
    val ingredient: RecipeIngredient,
    val isInProgress: Boolean,
)

class IngredientsViewModel(
    recipeId: Long,
    private val recipesRepository: RecipesRepository,
    private val shoppingListRepository: ShoppingListRepository
) : BaseViewModel(),
    IngredientsAddDeleteToFromShoppingListActionListener {

    private val _ingredients = MutableLiveData<StatusResult<List<IngredientsItem>>>()
    val ingredients: LiveData<StatusResult<List<IngredientsItem>>> = _ingredients

    private val ingredientsItemIdsInProgress = mutableSetOf<Long>()

    private var ingredientsResult: StatusResult<List<RecipeIngredient>> = EmptyResult()
        set(value) {
            field = value
            notifyUpdates()
        }

    private val listener: RecipeIngredientsListener = {
        ingredientsResult = if (it.isEmpty()) {
            EmptyResult()
        } else {
            SuccessResult(it)
        }
    }

    init {
        recipesRepository.addIngredientListener(listener)
        loadIngredients(recipeId)
    }

    private fun loadIngredients(recipeId: Long) {
        ingredientsResult = PendingResult()
        recipesRepository.loadIngredients(recipeId)
            .onError {
                ingredientsResult = ErrorResult(it)
            }
            .autoCancel()
    }

    override fun onCleared() {
        super.onCleared()
        recipesRepository.removeIngredientsListener(listener)
    }

    override fun invoke(ingredient: RecipeIngredient) {
        if (isInProgress(ingredient)) return
        addProgressTo(ingredient)

        if (!ingredient.isInShoppingList) {
            shoppingListRepository.addToShoppingList(ingredient)
                .onSuccess {
                    removeProgressFrom(ingredient)
                }
                .onError {
                    removeProgressFrom(ingredient)
                }
                .autoCancel()
        } else {
            shoppingListRepository.deleteFromShoppingList(ingredient)
                .onSuccess {
                    removeProgressFrom(ingredient)
                }
                .onError {
                    removeProgressFrom(ingredient)
                }
                .autoCancel()
        }
    }

    private fun addProgressTo(ingredient: RecipeIngredient) {
        ingredientsItemIdsInProgress.add(ingredient.id)
        notifyUpdates()
    }

    private fun removeProgressFrom(ingredient: RecipeIngredient) {
        ingredientsItemIdsInProgress.remove(ingredient.id)
        notifyUpdates()
    }

    private fun isInProgress(ingredient: RecipeIngredient): Boolean {
        return ingredientsItemIdsInProgress.contains(ingredient.id)
    }

    private fun notifyUpdates() {
        _ingredients.postValue(ingredientsResult.resultMap { ingredientsItemList ->
            ingredientsItemList.map { ingredient -> IngredientsItem(ingredient, isInProgress(ingredient)) } })
    }
}