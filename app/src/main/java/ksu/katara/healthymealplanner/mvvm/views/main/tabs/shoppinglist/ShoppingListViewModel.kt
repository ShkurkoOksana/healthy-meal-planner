package ksu.katara.healthymealplanner.mvvm.views.main.tabs.shoppinglist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.foundation.model.EmptyProgress
import ksu.katara.healthymealplanner.foundation.model.EmptyResult
import ksu.katara.healthymealplanner.foundation.model.ErrorResult
import ksu.katara.healthymealplanner.foundation.model.PendingResult
import ksu.katara.healthymealplanner.foundation.model.PercentageProgress
import ksu.katara.healthymealplanner.foundation.model.Progress
import ksu.katara.healthymealplanner.foundation.model.StatusResult
import ksu.katara.healthymealplanner.foundation.model.SuccessResult
import ksu.katara.healthymealplanner.foundation.model.isInProgress
import ksu.katara.healthymealplanner.foundation.navigator.Navigator
import ksu.katara.healthymealplanner.foundation.uiactions.UiActions
import ksu.katara.healthymealplanner.foundation.views.BaseViewModel
import ksu.katara.healthymealplanner.mvvm.model.shoppinglist.ShoppingListRepository
import ksu.katara.healthymealplanner.mvvm.model.shoppinglist.entity.ShoppingListRecipe
import ksu.katara.healthymealplanner.mvvm.model.shoppinglist.entity.ShoppingListRecipeIngredient
import ksu.katara.healthymealplanner.mvvm.views.main.tabs.recipecategories.recipedetails.RecipeDetailsFragment
import javax.inject.Inject
import kotlin.collections.set

data class ShoppingListRecipeItem(
    val recipe: ShoppingListRecipe,
    val shoppingListIngredients: MutableList<ShoppingListIngredientsItem>,
)

data class ShoppingListIngredientsItem(
    val shoppingListRecipeIngredient: ShoppingListRecipeIngredient,
    val isSelectInProgress: Boolean,
    val isDeleteInProgress: Progress,
)

data class ShoppingListIngredientsDeleteItem(
    val shoppingListRecipeId: Long,
    val shoppingListRecipeIngredientId: Long,
    var percentage: Int
)

@HiltViewModel
class ShoppingListViewModel @Inject constructor(
    private val navigator: Navigator,
    private val uiActions: UiActions,
    private val shoppingListRepository: ShoppingListRepository,
) : BaseViewModel(), ShoppingListRecipeActionListener {

    private val _shoppingList =
        MutableStateFlow<StatusResult<MutableList<ShoppingListRecipeItem>>>(EmptyResult())
    val shoppingList: MutableStateFlow<StatusResult<MutableList<ShoppingListRecipeItem>>> =
        _shoppingList

    private val _screenTitle = MutableLiveData<String>()
    val screenTitle: LiveData<String> = _screenTitle

    private val shoppingListIngredientsSelectItemIdsInProgress =
        mutableMapOf<Long, MutableList<Long>>()
    private val shoppingListIngredientsDeleteItemIdsInProgress =
        mutableListOf<ShoppingListIngredientsDeleteItem>()

    private var shoppingListResult: StatusResult<MutableList<ShoppingListRecipe>> = EmptyResult()
        set(value) {
            field = value
            notifyUpdates()
        }

    init {
        _screenTitle.value = uiActions.getString(R.string.shopping_list_title)
        loadShoppingList()
        viewModelScope.launch {
            shoppingListRepository.shoppingListListener()
                .collect {
                    shoppingListResult = if (it.isEmpty()) {
                        EmptyResult()
                    } else {
                        SuccessResult(it)
                    }
                }
        }
    }

    private fun loadShoppingList() {
        shoppingListResult = PendingResult()
        viewModelScope.launch {
            try {
                shoppingListRepository.loadShoppingList()
            } catch (e: Exception) {
                if (e !is CancellationException) shoppingListResult = ErrorResult(e)
            }
        }
    }

    private fun notifyUpdates() {
        _shoppingList.value = shoppingListResult.resultMap { shoppingListRecipes ->
            shoppingListRecipes.map { shoppingListRecipe ->
                ShoppingListRecipeItem(
                    shoppingListRecipe,
                    shoppingListRecipe.ingredients.map { shoppingListRecipeIngredient ->
                        ShoppingListIngredientsItem(
                            shoppingListRecipeIngredient,
                            isSelectInProgress(
                                shoppingListRecipe.recipe.id,
                                shoppingListRecipeIngredient.ingredient.id
                            ),
                            isDeleteInProgress(
                                shoppingListRecipe.recipe.id,
                                shoppingListRecipeIngredient.ingredient.id
                            ),
                        )
                    }.toMutableList()
                )
            }.toMutableList()
        }
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
        if (isSelectInProgress(
                shoppingListRecipe.recipe.id,
                shoppingListRecipeIngredient.ingredient.id
            )
        ) return
        addSelectProgressTo(
            shoppingListRecipe.recipe.id,
            shoppingListRecipeIngredient.ingredient.id
        )
        viewModelScope.launch {
            try {
                shoppingListRepository.selectIngredient(
                    shoppingListRecipe,
                    shoppingListRecipeIngredient,
                    isChecked
                )
                removeSelectProgressFrom(
                    shoppingListRecipe.recipe.id,
                    shoppingListRecipeIngredient.ingredient.id
                )
            } catch (e: Exception) {
                if (e !is CancellationException) removeSelectProgressFrom(
                    shoppingListRecipe.recipe.id,
                    shoppingListRecipeIngredient.ingredient.id
                )
            }
        }
    }

    override fun onShoppingListIngredientsRecipeDelete(
        shoppingListRecipe: ShoppingListRecipe,
        shoppingListRecipeIngredient: ShoppingListRecipeIngredient
    ) {
        if (isDeleteInProgress(
                shoppingListRecipe.recipe.id,
                shoppingListRecipeIngredient.ingredient.id
            ).isInProgress()
        ) return
        addDeleteProgressTo(
            shoppingListRecipe.recipe.id,
            shoppingListRecipeIngredient.ingredient.id
        )
        viewModelScope.launch {
            try {
                var element: ShoppingListIngredientsDeleteItem
                shoppingListRepository.deleteIngredient(
                    shoppingListRecipe.recipe.id,
                    shoppingListRecipeIngredient.ingredient
                ).collect { percentage ->
                    element = shoppingListIngredientsDeleteItemIdsInProgress.first {
                        it.shoppingListRecipeId == shoppingListRecipe.recipe.id &&
                                it.shoppingListRecipeIngredientId == shoppingListRecipeIngredient.ingredient.id
                    }
                    element.percentage = percentage
                }
                removeDeleteProgressFrom(
                    shoppingListRecipe.recipe.id,
                    shoppingListRecipeIngredient.ingredient.id
                )
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    removeDeleteProgressFrom(
                        shoppingListRecipe.recipe.id,
                        shoppingListRecipeIngredient.ingredient.id
                    )
                    val message =
                        uiActions.getString(R.string.cant_delete_ingredient_from_shopping_list)
                    uiActions.toast(message)
                }
            }
        }
        notifyUpdates()
    }

    private fun addSelectProgressTo(
        shoppingListRecipeId: Long,
        shoppingListRecipeIngredientId: Long
    ) {
        if (shoppingListIngredientsSelectItemIdsInProgress.containsKey(shoppingListRecipeId)) {
            val list = shoppingListIngredientsSelectItemIdsInProgress[shoppingListRecipeId]
                ?: mutableListOf()
            list.add(shoppingListRecipeIngredientId)
        } else {
            shoppingListIngredientsSelectItemIdsInProgress[shoppingListRecipeId] =
                mutableListOf(shoppingListRecipeIngredientId)
        }
        notifyUpdates()
    }

    private fun removeSelectProgressFrom(
        shoppingListRecipeId: Long,
        shoppingListRecipeIngredientId: Long
    ) {
        if (shoppingListIngredientsSelectItemIdsInProgress.contains(shoppingListRecipeId)) {
            val list = shoppingListIngredientsSelectItemIdsInProgress[shoppingListRecipeId]
            val indexToDelete = list?.indexOfFirst { it == shoppingListRecipeIngredientId } ?: -1
            if (indexToDelete != -1) {
                list!!.removeAt(indexToDelete)
            }
        }
        notifyUpdates()
    }

    private fun isSelectInProgress(
        shoppingListRecipeId: Long,
        shoppingListRecipeIngredientId: Long
    ): Boolean {
        return if (shoppingListIngredientsSelectItemIdsInProgress[shoppingListRecipeId]?.contains(
                shoppingListRecipeIngredientId
            ) == null
        ) {
            false
        } else {
            shoppingListIngredientsSelectItemIdsInProgress[shoppingListRecipeId]!!.contains(
                shoppingListRecipeIngredientId
            )
        }
    }

    private fun addDeleteProgressTo(
        shoppingListRecipeId: Long,
        shoppingListRecipeIngredientId: Long
    ) {
        shoppingListIngredientsDeleteItemIdsInProgress.add(
            ShoppingListIngredientsDeleteItem(
                shoppingListRecipeId,
                shoppingListRecipeIngredientId,
                0
            )
        )
        notifyUpdates()
    }

    private fun removeDeleteProgressFrom(
        shoppingListRecipeId: Long,
        shoppingListRecipeIngredientId: Long
    ) {
        val indexToDelete =
            shoppingListIngredientsDeleteItemIdsInProgress.indexOfFirst { it.shoppingListRecipeId == shoppingListRecipeId && it.shoppingListRecipeIngredientId == shoppingListRecipeIngredientId }
        if (indexToDelete != -1) {
            shoppingListIngredientsDeleteItemIdsInProgress.removeAt(indexToDelete)
        }
        notifyUpdates()
    }

    private fun isDeleteInProgress(
        shoppingListRecipeId: Long,
        shoppingListRecipeIngredientId: Long
    ): Progress {
        val element = shoppingListIngredientsDeleteItemIdsInProgress.firstOrNull() {
            it.shoppingListRecipeId == shoppingListRecipeId &&
                    it.shoppingListRecipeIngredientId == shoppingListRecipeIngredientId
        }
        return if (element != null) PercentageProgress(element.percentage) else EmptyProgress
    }

    fun loadAgain() {
        loadShoppingList()
    }
}
