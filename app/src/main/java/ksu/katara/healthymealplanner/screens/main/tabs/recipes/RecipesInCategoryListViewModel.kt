package ksu.katara.healthymealplanner.screens.main.tabs.recipes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ksu.katara.healthymealplanner.model.recipes.RecipesInCategoryListener
import ksu.katara.healthymealplanner.model.recipes.RecipesRepository
import ksu.katara.healthymealplanner.model.recipes.entities.Recipe
import ksu.katara.healthymealplanner.screens.base.BaseViewModel
import ksu.katara.healthymealplanner.screens.base.Event
import ksu.katara.healthymealplanner.tasks.EmptyResult
import ksu.katara.healthymealplanner.tasks.ErrorResult
import ksu.katara.healthymealplanner.tasks.PendingResult
import ksu.katara.healthymealplanner.tasks.StatusResult
import ksu.katara.healthymealplanner.tasks.SuccessResult

data class RecipesInCategoryListItem(
    val recipe: Recipe,
    val isInProgress: Boolean
)

class RecipesInCategoryViewModel(
    recipeCategoryId: Long,
    private val recipesRepository: RecipesRepository,
) : BaseViewModel(), RecipesInCategoryActionListener {

    private val _recipeCategory = MutableLiveData<Long>()
    val recipeCategory: LiveData<Long> = _recipeCategory

    private val _recipesInCategory = MutableLiveData<StatusResult<List<RecipesInCategoryListItem>>>()
    val recipesInCategory: LiveData<StatusResult<List<RecipesInCategoryListItem>>> = _recipesInCategory

    private val _actionShowDetails = MutableLiveData<Event<Recipe>>()
    val actionShowDetails: LiveData<Event<Recipe>> = _actionShowDetails

    private val recipeIdsInProgress = mutableSetOf<Long>()
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
        _recipeCategory.value = recipeCategoryId
        recipesRepository.addRecipeInCategoryListener(listener)
        loadRecipesInCategory(recipeCategoryId)
    }

    private fun loadRecipesInCategory(recipeCategoryId: Long) {
        recipesInCategoryResult = PendingResult()
        recipesRepository.loadRecipesInCategory(recipeCategoryId)
            .onError {
                recipesInCategoryResult = ErrorResult(it)
            }
            .autoCancel()
    }

    override fun onCleared() {
        super.onCleared()
        recipesRepository.removeRecipeInCategoryListener(listener)
    }

    private fun isInProgress(recipe: Recipe): Boolean {
        return recipeIdsInProgress.contains(recipe.id)
    }

    private fun notifyUpdates() {
        _recipesInCategory.postValue(recipesInCategoryResult.resultMap { recipes ->
            recipes.map { recipe ->
                RecipesInCategoryListItem(recipe, isInProgress(recipe)) } })
    }

    override fun invoke(recipe: Recipe) {
        _actionShowDetails.value = Event(recipe)
    }
}