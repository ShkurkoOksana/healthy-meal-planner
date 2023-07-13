package ksu.katara.healthymealplanner.mvvm.views.main.tabs.mealplan.addrecipes

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
import ksu.katara.healthymealplanner.mvvm.model.addrecipes.AddRecipesListener
import ksu.katara.healthymealplanner.mvvm.model.addrecipes.AddRecipesRepository
import ksu.katara.healthymealplanner.mvvm.model.mealplan.MealPlanForDateRecipesRepository
import ksu.katara.healthymealplanner.mvvm.model.recipes.entities.Recipe
import ksu.katara.healthymealplanner.mvvm.views.main.tabs.home.MealTypes
import ksu.katara.healthymealplanner.mvvm.views.main.tabs.mealplan.addrecipes.AddRecipesFragment.Screen
import java.util.Date

data class AddRecipesItem(
    val recipe: Recipe,
    val isDeleteInProgress: Boolean,
)

class AddRecipesListViewModel(
    screen: Screen,
    private val navigator: Navigator,
    private val uiActions: UiActions,
    private val addRecipesRepository: AddRecipesRepository,
    private val mealPlanForDateRecipesRepository: MealPlanForDateRecipesRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel(), AddRecipesActionListener {

    private val _addRecipes = MutableLiveResult<List<AddRecipesItem>>()
    val addRecipes: LiveResult<List<AddRecipesItem>> = _addRecipes

    private val _screenTitle = MutableLiveData<String>()
    val screenTitle: LiveData<String> = _screenTitle

    private val addRecipesDeleteItemIdsInProgress = mutableSetOf<Long>()

    private var addRecipesResult: StatusResult<List<Recipe>> = EmptyResult()
        set(value) {
            field = value
            notifyUpdates()
        }

    private val addRecipesListener: AddRecipesListener = {
        addRecipesResult = if (it.isEmpty()) {
            EmptyResult()
        } else {
            SuccessResult(it)
        }
    }

    private val mealType = screen.mealType
    private val selectedDate = screen.selectedDate

    init {
        _screenTitle.value = uiActions.getString(R.string.add_recipe_title)
        addRecipesRepository.addAddRecipesListener(addRecipesListener)
        loadAddRecipes(selectedDate, mealType)
    }

    private fun loadAddRecipes(selectedDate: Date, mealType: MealTypes) {
        addRecipesResult = PendingResult()
        viewModelScope.launch {
            try {
                addRecipesRepository.loadAddRecipes(selectedDate, mealType)
            } catch (e: Exception) {
                if (e !is CancellationException) addRecipesResult = ErrorResult(e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        addRecipesRepository.removeAddRecipesListener(addRecipesListener)
    }

    override fun onAddRecipePressed(recipe: Recipe) {
        if (isDeleteInProgress(recipe)) return
        addDeleteProgressTo(recipe)
        viewModelScope.launch {
            try {
                mealPlanForDateRecipesRepository.mealPlanForDateRecipesAddRecipe(selectedDate, mealType, recipe)
                viewModelScope.launch {
                    try {
                        addRecipesRepository.addRecipesDeleteRecipe(recipe)
                        removeDeleteProgressFrom(recipe)
                    } catch (e: Exception) {
                        if (e !is CancellationException) {
                            val message = uiActions.getString(R.string.cant_delete_recipe_from_add_recipes)
                            uiActions.toast(message)
                        }
                    }
                }
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    removeDeleteProgressFrom(recipe)
                    val message = uiActions.getString(R.string.cant_add_recipe_to_meal_plan)
                    uiActions.toast(message)
                }
            }
        }
    }

    private fun addDeleteProgressTo(recipe: Recipe) {
        addRecipesDeleteItemIdsInProgress.add(recipe.id)
        notifyUpdates()
    }

    private fun removeDeleteProgressFrom(recipe: Recipe) {
        addRecipesDeleteItemIdsInProgress.remove(recipe.id)
        notifyUpdates()
    }

    private fun isDeleteInProgress(recipe: Recipe): Boolean {
        return addRecipesDeleteItemIdsInProgress.contains(recipe.id)
    }

    private fun notifyUpdates() {
        _addRecipes.postValue(addRecipesResult.resultMap { addRecipes ->
            addRecipes.map { recipe -> AddRecipesItem(recipe, isDeleteInProgress(recipe)) }.toMutableList()
        })
    }

    fun tryAgain() {
        loadAddRecipes(selectedDate, mealType)
    }
}