package ksu.katara.healthymealplanner.screens.main.tabs.home.recipe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ksu.katara.healthymealplanner.model.recipes.RecipesRepository
import ksu.katara.healthymealplanner.model.recipes.entities.RecipeDetails
import ksu.katara.healthymealplanner.screens.base.BaseViewModel
import ksu.katara.healthymealplanner.tasks.EmptyResult
import ksu.katara.healthymealplanner.tasks.PendingResult
import ksu.katara.healthymealplanner.tasks.StatusResult
import ksu.katara.healthymealplanner.tasks.SuccessResult

class RecipeViewModel(
    private val recipeId: Long,
    private val recipeRepository: RecipesRepository,
) : BaseViewModel() {

    private val _recipeState = MutableLiveData<State>()
    val recipeState: LiveData<State> = _recipeState

    private val currentState: State get() = recipeState.value!!

    init {
        _recipeState.value = State(recipeFullDetailsResult = EmptyResult())
    }

    fun loadRecipe() {
        if (currentState.recipeFullDetailsResult !is EmptyResult) return

        _recipeState.value = currentState.copy(recipeFullDetailsResult = PendingResult())

        recipeRepository.getRecipeDetailsById(recipeId)
            .onSuccess {
                _recipeState.value = currentState.copy(recipeFullDetailsResult = SuccessResult(it))
            }
            .onError {
            }.autoCancel()
    }

    data class State(
        val recipeFullDetailsResult: StatusResult<RecipeDetails>,
    ) {

        val showContent: Boolean get() = recipeFullDetailsResult is SuccessResult
        val showProgress: Boolean get() = recipeFullDetailsResult is PendingResult
    }
}