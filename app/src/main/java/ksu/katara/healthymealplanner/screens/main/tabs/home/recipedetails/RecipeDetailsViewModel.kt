package ksu.katara.healthymealplanner.screens.main.tabs.home.recipedetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.model.recipes.RecipesRepository
import ksu.katara.healthymealplanner.model.recipes.entities.RecipeDetails
import ksu.katara.healthymealplanner.screens.base.BaseViewModel
import ksu.katara.healthymealplanner.screens.base.Event
import ksu.katara.healthymealplanner.tasks.EmptyResult
import ksu.katara.healthymealplanner.tasks.PendingResult
import ksu.katara.healthymealplanner.tasks.StatusResult
import ksu.katara.healthymealplanner.tasks.SuccessResult

class RecipeDetailsViewModel(
    private val recipeId: Long,
    private val recipesRepository: RecipesRepository,
) : BaseViewModel() {
    private val _recipeDetails = MutableLiveData<StatusResult<RecipeDetails>>()
    val recipeDetails: LiveData<StatusResult<RecipeDetails>> = _recipeDetails

    private val _actionShowToast = MutableLiveData<Event<Int>>()
    val actionShowToast: LiveData<Event<Int>> = _actionShowToast

    private var recipeDetailsResult: StatusResult<RecipeDetails> = EmptyResult()
        set(value) {
            field = value
            notifyUpdates()
        }

    init {
        loadRecipeDetails()
    }

    private fun loadRecipeDetails() {
        recipeDetailsResult = PendingResult()
        recipesRepository.loadRecipeDetails(recipeId)
            .onSuccess {
                recipeDetailsResult = SuccessResult(it)
            }
            .onError {
                _actionShowToast.value = Event(R.string.cant_load_recipe_details)
            }
            .autoCancel()
    }

    private fun notifyUpdates() {
        _recipeDetails.postValue(recipeDetailsResult)
    }
}
