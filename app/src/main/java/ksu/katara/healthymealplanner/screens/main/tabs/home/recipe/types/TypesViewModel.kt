package ksu.katara.healthymealplanner.screens.main.tabs.home.recipe.types

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ksu.katara.healthymealplanner.model.recipes.RecipesRepository
import ksu.katara.healthymealplanner.screens.base.BaseViewModel
import ksu.katara.healthymealplanner.tasks.EmptyResult
import ksu.katara.healthymealplanner.tasks.ErrorResult
import ksu.katara.healthymealplanner.tasks.PendingResult
import ksu.katara.healthymealplanner.tasks.StatusResult
import ksu.katara.healthymealplanner.tasks.SuccessResult

class RecipeTypesListViewModel (
    private val recipeId: Long,
    private val recipesRepository: RecipesRepository,
) : BaseViewModel() {

    private val _recipeTypes = MutableLiveData<StatusResult<List<String>>>()
    val recipeTypes: LiveData<StatusResult<List<String>>> = _recipeTypes

    private var recipeTypesResult: StatusResult<List<String>> = EmptyResult()
        set(value) {
            field = value
            notifyUpdates()
        }

    init {
        loadRecipeTypes()
    }

    private fun loadRecipeTypes() {
        recipeTypesResult = PendingResult()
        recipesRepository.loadRecipeTypes(recipeId)
            .onSuccess {
                _recipeTypes.value = SuccessResult(it)
            }
            .onError {
                recipeTypesResult = ErrorResult(it)
            }
            .autoCancel()
    }

    private fun notifyUpdates() {
        _recipeTypes.postValue(recipeTypesResult)
    }
}
