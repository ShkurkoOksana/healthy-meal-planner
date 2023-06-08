package ksu.katara.healthymealplanner.screens.main.tabs.home.recipedetails.ingredients

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.model.recipes.RecipeIngredientsListener
import ksu.katara.healthymealplanner.model.recipes.RecipesRepository
import ksu.katara.healthymealplanner.model.recipes.entities.RecipeIngredient
import ksu.katara.healthymealplanner.model.shoppinglist.ShoppingListRepository
import ksu.katara.healthymealplanner.screens.base.BaseViewModel
import ksu.katara.healthymealplanner.screens.base.Event
import ksu.katara.healthymealplanner.tasks.EmptyResult
import ksu.katara.healthymealplanner.tasks.PendingResult
import ksu.katara.healthymealplanner.tasks.StatusResult
import ksu.katara.healthymealplanner.tasks.SuccessResult

data class IngredientsItem(
    val ingredient: RecipeIngredient,
    val isInProgress: Boolean,
)

class IngredientsViewModel(
    private val recipeId: Long,
    private val recipesRepository: RecipesRepository,
    private val shoppingListRepository: ShoppingListRepository,
) : BaseViewModel(),
    IngredientSelectedActionListener {

    private val _ingredients = MutableLiveData<StatusResult<List<IngredientsItem>>>()
    val ingredients: LiveData<StatusResult<List<IngredientsItem>>> = _ingredients

    private val _isAllIngredientsSelected = MutableLiveData<StatusResult<Boolean>>()
    val isAllIngredientsSelected: LiveData<StatusResult<Boolean>> = _isAllIngredientsSelected

    private val _actionShowToast = MutableLiveData<Event<Int>>()
    val actionShowToast: LiveData<Event<Int>> = _actionShowToast

    private val ingredientsItemIdsInProgress = mutableSetOf<Long>()

    private var allIngredients: List<RecipeIngredient> = mutableListOf()
    private var isAddIngredientPressed = false

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
            .onSuccess {
                allIngredients = it
            }
            .onError {
                _actionShowToast.value = Event(R.string.cant_load_ingredients)
            }
            .autoCancel()

        recipesRepository.isAllIngredientsSelected(recipeId)
            .onSuccess {
                _isAllIngredientsSelected.value = SuccessResult(it)
            }
            .autoCancel()
    }

    override fun onCleared() {
        super.onCleared()
        recipesRepository.removeIngredientsListener(listener)
    }

    override fun invoke(ingredient: RecipeIngredient, isSelected: Boolean) {
        isAddIngredientPressed = true
        if (isInProgress(ingredient)) return
        addProgressTo(ingredient)
        recipesRepository.setIngredientSelected(recipeId, ingredient, isSelected)
            .onSuccess {
                removeProgressFrom(ingredient)
                if (it) {
                    _isAllIngredientsSelected.value = SuccessResult(true)
                    shoppingListRepository.shoppingListIngredientsAddAllIngredients(recipeId, isSelected)
                } else {
                    _isAllIngredientsSelected.value = SuccessResult(false)
                    isAddIngredientPressed = false
                }
            }
            .onError {
                removeProgressFrom(ingredient)
            }
            .autoCancel()

        if (isSelected) {
            shoppingListRepository.shoppingListIngredientsAddIngredient(recipeId, ingredient)
                .onError {
                    _actionShowToast.value = Event(R.string.cant_add_ingredient_to_shopping_list)
                }
                .autoCancel()
        } else {
            shoppingListRepository.shoppingListIngredientsDeleteIngredient(recipeId, ingredient)
                .onError {
                    _actionShowToast.value = Event(R.string.cant_delete_ingredient_from_shopping_list)
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
            ingredientsItemList.map { ingredient -> IngredientsItem(ingredient, isInProgress(ingredient)) }
        })
    }

    fun setAllIngredientsSelected(isSelected: Boolean) {
        _isAllIngredientsSelected.value = PendingResult()
        allIngredients.forEach { ingredient -> if (isInProgress(ingredient)) return }
        if (isAddIngredientPressed) {
            var count = 0
            allIngredients.forEach { if (!it.isInShoppingList) count++ }
            if (count == 0) {
                _isAllIngredientsSelected.value = SuccessResult(isSelected)
            } else {
                _isAllIngredientsSelected.value = EmptyResult()
            }
            isAddIngredientPressed = false
        } else {
            recipesRepository.setAllIngredientsSelected(recipeId, isSelected)
                .onSuccess {
                    _isAllIngredientsSelected.value = SuccessResult(isSelected)
                    allIngredients.forEach { ingredient -> addProgressTo(ingredient) }
                    allIngredients.forEach { ingredient -> removeProgressFrom(ingredient) }
                }
                .onError {
                    _actionShowToast.value = Event(R.string.cant_set_all_ingredients_selected)
                }
                .autoCancel()
        }
        shoppingListRepository.shoppingListIngredientsAddAllIngredients(recipeId, isSelected)
            .onError {
                _actionShowToast.value = Event(R.string.cant_add_all_ingredient_to_shopping_list)
            }
            .autoCancel()
    }
}