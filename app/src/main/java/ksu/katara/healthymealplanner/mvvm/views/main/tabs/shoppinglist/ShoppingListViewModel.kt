package ksu.katara.healthymealplanner.mvvm.views.main.tabs.shoppinglist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.foundation.model.EmptyResult
import ksu.katara.healthymealplanner.foundation.model.PendingResult
import ksu.katara.healthymealplanner.foundation.model.StatusResult
import ksu.katara.healthymealplanner.foundation.model.SuccessResult
import ksu.katara.healthymealplanner.foundation.navigator.Navigator
import ksu.katara.healthymealplanner.foundation.uiactions.UiActions
import ksu.katara.healthymealplanner.foundation.views.BaseViewModel
import ksu.katara.healthymealplanner.foundation.views.LiveResult
import ksu.katara.healthymealplanner.foundation.views.MutableLiveResult
import ksu.katara.healthymealplanner.mvvm.model.recipes.RecipesRepository
import ksu.katara.healthymealplanner.mvvm.model.shoppinglist.ShoppingListListener
import ksu.katara.healthymealplanner.mvvm.model.shoppinglist.ShoppingListRepository
import ksu.katara.healthymealplanner.mvvm.model.shoppinglist.entity.ShoppingListRecipe
import ksu.katara.healthymealplanner.mvvm.model.shoppinglist.entity.ShoppingListRecipeIngredient
import ksu.katara.healthymealplanner.mvvm.views.main.tabs.home.recipedetails.RecipeDetailsFragment
import ksu.katara.healthymealplanner.mvvm.views.main.tabs.shoppinglist.ShoppingListFragment.Screen

data class ShoppingListRecipeItem(
    val recipe: ShoppingListRecipe,
    val shoppingListIngredients: MutableList<ShoppingListIngredientsItem>,
)

data class ShoppingListIngredientsItem(
    val shoppingListRecipeIngredient: ShoppingListRecipeIngredient,
    val isSelectInProgress: Boolean,
    val isDeleteInProgress: Boolean,
)

class ShoppingListViewModel(
    screen: Screen,
    private val navigator: Navigator,
    private val uiActions: UiActions,
    private val shoppingListRepository: ShoppingListRepository,
    private val recipesRepository: RecipesRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(), ShoppingListRecipeActionListener {

    private val _shoppingList = MutableLiveResult<MutableList<ShoppingListRecipeItem>>()
    val shoppingList: LiveResult<MutableList<ShoppingListRecipeItem>> = _shoppingList

    private val _screenTitle = MutableLiveData<String>()
    val screenTitle: LiveData<String> = _screenTitle

    private val shoppingListIngredientsSelectItemIdsInProgress = mutableMapOf<Long, MutableList<Long>>()
    private val shoppingListIngredientsDeleteItemIdsInProgress = mutableMapOf<Long, MutableList<Long>>()

    private var shoppingListResult: StatusResult<MutableList<ShoppingListRecipe>> = EmptyResult()
        set(value) {
            field = value
            notifyUpdates()
        }

    private val listener: ShoppingListListener = {
        shoppingListResult = if (it.isEmpty()) {
            EmptyResult()
        } else {
            SuccessResult(it)
        }
    }

    init {
        _screenTitle.value = uiActions.getString(R.string.shopping_list_title)
        shoppingListRepository.addShoppingListListener(listener)
        loadShoppingList()
    }

    private fun loadShoppingList() {
        shoppingListResult = PendingResult()
        shoppingListRepository.loadShoppingList()
            .onError {
                val message = uiActions.getString(R.string.cant_load_shopping_list)
                uiActions.toast(message)
            }
            .autoCancel()
    }

    override fun onCleared() {
        super.onCleared()
        shoppingListRepository.removeShoppingListListener(listener)
    }

    private fun notifyUpdates() {
        _shoppingList.postValue(shoppingListResult.resultMap { shoppingListRecipes ->
            shoppingListRecipes.map { shoppingListRecipe ->
                ShoppingListRecipeItem(
                    shoppingListRecipe,
                    shoppingListRecipe.shoppingListIngredients.map { shoppingListRecipeIngredient ->
                        ShoppingListIngredientsItem(
                            shoppingListRecipeIngredient,
                            isSelectInProgress(shoppingListRecipe.recipe.id, shoppingListRecipeIngredient.recipeIngredient.id),
                            isDeleteInProgress(shoppingListRecipe.recipe.id, shoppingListRecipeIngredient.recipeIngredient.id),
                        )
                    }.toMutableList()
                )
            }.toMutableList()
        })
    }

    override fun onShoppingListRecipeDetails(shoppingListRecipe: ShoppingListRecipe) {
        val screen = RecipeDetailsFragment.Screen(shoppingListRecipe.recipe)
        navigator.launch(R.id.shoppingListFragment, RecipeDetailsFragment.createArgs(screen))
    }

    override fun onShoppingListIngredientsRecipeSelect(
        shoppingListRecipe: ShoppingListRecipe,
        shoppingListRecipeIngredient: ShoppingListRecipeIngredient,
        isChecked: Boolean
    ) {
        if (isSelectInProgress(shoppingListRecipe.recipe.id, shoppingListRecipeIngredient.recipeIngredient.id)) return
        addSelectProgressTo(shoppingListRecipe.recipe.id, shoppingListRecipeIngredient.recipeIngredient.id)
        shoppingListRepository.shoppingListIngredientsSelectIngredient(shoppingListRecipe, shoppingListRecipeIngredient, isChecked)
            .onSuccess {
                removeSelectProgressFrom(shoppingListRecipe.recipe.id, shoppingListRecipeIngredient.recipeIngredient.id)
            }
            .onError {
                removeSelectProgressFrom(shoppingListRecipe.recipe.id, shoppingListRecipeIngredient.recipeIngredient.id)
            }
            .autoCancel()
    }

    override fun onShoppingListIngredientsRecipeDelete(
        shoppingListRecipe: ShoppingListRecipe,
        shoppingListRecipeIngredient: ShoppingListRecipeIngredient
    ) {
        if (isDeleteInProgress(shoppingListRecipe.recipe.id, shoppingListRecipeIngredient.recipeIngredient.id)) return
        addDeleteProgressTo(shoppingListRecipe.recipe.id, shoppingListRecipeIngredient.recipeIngredient.id)
        shoppingListRepository.shoppingListIngredientsDeleteIngredient(shoppingListRecipe.recipe.id, shoppingListRecipeIngredient.recipeIngredient)
            .onSuccess {
                removeDeleteProgressFrom(shoppingListRecipe.recipe.id, shoppingListRecipeIngredient.recipeIngredient.id)
            }
            .onError {
                val message = uiActions.getString(R.string.cant_delete_ingredient_from_shopping_list)
                uiActions.toast(message)
            }
            .autoCancel()
        recipesRepository.setIngredientSelected(shoppingListRecipe.recipe.id, shoppingListRecipeIngredient.recipeIngredient, false)
    }

    private fun addSelectProgressTo(shoppingListRecipeId: Long, shoppingListRecipeIngredientId: Long) {
        if (shoppingListIngredientsSelectItemIdsInProgress.containsKey(shoppingListRecipeId)) {
            val list = shoppingListIngredientsSelectItemIdsInProgress[shoppingListRecipeId] ?: mutableListOf()
            list.add(shoppingListRecipeIngredientId)
        } else {
            shoppingListIngredientsSelectItemIdsInProgress[shoppingListRecipeId] = mutableListOf(shoppingListRecipeIngredientId)
        }
        notifyUpdates()
    }

    private fun removeSelectProgressFrom(shoppingListRecipeId: Long, shoppingListRecipeIngredientId: Long) {
        if (shoppingListIngredientsSelectItemIdsInProgress.contains(shoppingListRecipeId)) {
            val list = shoppingListIngredientsSelectItemIdsInProgress[shoppingListRecipeId]
            val indexToDelete = list?.indexOfFirst { it == shoppingListRecipeIngredientId } ?: -1
            if (indexToDelete != -1) {
                list!!.removeAt(indexToDelete)
            }
        }
        notifyUpdates()
    }

    private fun isSelectInProgress(shoppingListRecipeId: Long, shoppingListRecipeIngredientId: Long): Boolean {
        return if (shoppingListIngredientsSelectItemIdsInProgress[shoppingListRecipeId]?.contains(shoppingListRecipeIngredientId) == null) {
            false
        } else {
            shoppingListIngredientsSelectItemIdsInProgress[shoppingListRecipeId]!!.contains(shoppingListRecipeIngredientId)
        }
    }

    private fun addDeleteProgressTo(shoppingListRecipeId: Long, shoppingListRecipeIngredientId: Long) {
        if (shoppingListIngredientsDeleteItemIdsInProgress.containsKey(shoppingListRecipeId)) {
            val list = shoppingListIngredientsDeleteItemIdsInProgress[shoppingListRecipeId] ?: mutableListOf()
            list.add(shoppingListRecipeIngredientId)
        } else {
            shoppingListIngredientsDeleteItemIdsInProgress[shoppingListRecipeId] = mutableListOf(shoppingListRecipeIngredientId)
        }
        notifyUpdates()
    }

    private fun removeDeleteProgressFrom(shoppingListRecipeId: Long, shoppingListRecipeIngredientId: Long) {
        if (shoppingListIngredientsDeleteItemIdsInProgress.contains(shoppingListRecipeId)) {
            val list = shoppingListIngredientsDeleteItemIdsInProgress[shoppingListRecipeId]
            val indexToDelete = list?.indexOfFirst { it == shoppingListRecipeIngredientId } ?: -1
            if (indexToDelete != -1) {
                list!!.removeAt(indexToDelete)
            }
        }
        notifyUpdates()
    }

    private fun isDeleteInProgress(shoppingListRecipeId: Long, shoppingListRecipeIngredientId: Long): Boolean {
        return if (shoppingListIngredientsDeleteItemIdsInProgress[shoppingListRecipeId]?.contains(shoppingListRecipeIngredientId) == null) {
            false
        } else {
            shoppingListIngredientsDeleteItemIdsInProgress[shoppingListRecipeId]!!.contains(shoppingListRecipeIngredientId)
        }
    }
}
