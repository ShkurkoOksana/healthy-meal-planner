package ksu.katara.healthymealplanner.screens.main.tabs.mealplan.mealplanfordate.addrecipe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ksu.katara.healthymealplanner.model.addrecipes.AddRecipesListener
import ksu.katara.healthymealplanner.model.addrecipes.AddRecipesRepository
import ksu.katara.healthymealplanner.model.meal.enum.MealTypes
import ksu.katara.healthymealplanner.model.mealplan.MealPlanForDateRecipesRepository
import ksu.katara.healthymealplanner.model.recipes.entities.Recipe
import ksu.katara.healthymealplanner.screens.base.BaseViewModel
import ksu.katara.healthymealplanner.tasks.EmptyResult
import ksu.katara.healthymealplanner.tasks.ErrorResult
import ksu.katara.healthymealplanner.tasks.PendingResult
import ksu.katara.healthymealplanner.tasks.StatusResult
import ksu.katara.healthymealplanner.tasks.SuccessResult

data class AddRecipesItem(
    val recipe: Recipe,
    val isDeleteInProgress: Boolean,
)

class AddRecipesListViewModel(
    private val selectedDate: String,
    private val mealType: MealTypes,
    private val addRecipesRepository: AddRecipesRepository,
    private val mealPlanForDateRecipesRepository: MealPlanForDateRecipesRepository,
) : BaseViewModel(), OnAddRecipesActionListener {

    private val _addRecipes = MutableLiveData<StatusResult<MutableList<AddRecipesItem>>>()
    val addRecipes: LiveData<StatusResult<MutableList<AddRecipesItem>>> = _addRecipes

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

    init {
        addRecipesRepository.addAddRecipesListener(addRecipesListener)
        loadAddRecipes(selectedDate, mealType)
    }

    private fun loadAddRecipes(selectedDate: String, mealType: MealTypes) {
        addRecipesResult = PendingResult()
        addRecipesRepository.loadAddRecipes(selectedDate, mealType)
            .onError {
                addRecipesResult = ErrorResult(it)
            }
            .autoCancel()
    }

    override fun onCleared() {
        super.onCleared()
        addRecipesRepository.removeAddRecipesListener(addRecipesListener)
    }

    override fun onAddRecipesItemDelete(recipe: Recipe) {
        if (isDeleteInProgress(recipe)) return
        addDeleteProgressTo(recipe)
        mealPlanForDateRecipesRepository.mealPlanForDateRecipesAddRecipe(selectedDate, mealType, recipe)
            .onError {
            }
            .onSuccess {
            }
            .autoCancel()

        addRecipesRepository.addRecipesDeleteRecipe(recipe)
            .onError {

            }
            .onSuccess {
                removeDeleteProgressFrom(recipe)
            }
            .autoCancel()
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
}