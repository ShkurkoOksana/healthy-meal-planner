package ksu.katara.healthymealplanner.screens.main.tabs.recipes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.model.recipes.RecipesInCategoryListener
import ksu.katara.healthymealplanner.model.recipes.RecipesRepository
import ksu.katara.healthymealplanner.model.recipes.entities.Recipe
import ksu.katara.healthymealplanner.screens.base.BaseViewModel
import ksu.katara.healthymealplanner.screens.base.Event
import ksu.katara.healthymealplanner.tasks.EmptyResult
import ksu.katara.healthymealplanner.tasks.PendingResult
import ksu.katara.healthymealplanner.tasks.StatusResult
import ksu.katara.healthymealplanner.tasks.SuccessResult

class RecipesInCategoryViewModel(
    recipeCategoryId: Long,
    private val recipesRepository: RecipesRepository,
) : BaseViewModel(), RecipesInCategoryActionListener {

    private val _recipesInCategory = MutableLiveData<StatusResult<List<Recipe>>>()
    val recipesInCategory: LiveData<StatusResult<List<Recipe>>> = _recipesInCategory

    private val _actionShowToast = MutableLiveData<Event<Int>>()
    val actionShowToast: LiveData<Event<Int>> = _actionShowToast

    private val _actionShowDetails = MutableLiveData<Event<Recipe>>()
    val actionShowDetails: LiveData<Event<Recipe>> = _actionShowDetails

    private var recipesInCategoryResult: StatusResult<List<Recipe>> = EmptyResult()
        set(value) {
            field = value
            notifyUpdates()
        }

    private val listener: RecipesInCategoryListener = {
        recipesInCategoryResult = if (it.isEmpty()) {
            EmptyResult()
        } else {
            SuccessResult(it)
        }
    }

    init {
        recipesRepository.addRecipeInCategoryListener(listener)
        loadRecipesInCategory(recipeCategoryId)
    }

    private fun loadRecipesInCategory(recipeCategoryId: Long) {
        recipesInCategoryResult = PendingResult()
        recipesRepository.loadRecipesInCategory(recipeCategoryId)
            .onError {
                _actionShowToast.value = Event(R.string.cant_load_recipe_in_category)
            }
            .autoCancel()
    }

    override fun onCleared() {
        super.onCleared()
        recipesRepository.removeRecipeInCategoryListener(listener)
    }

    private fun notifyUpdates() {
        _recipesInCategory.postValue(recipesInCategoryResult)
    }

    override fun invoke(recipe: Recipe) {
        _actionShowDetails.value = Event(recipe)
    }
}