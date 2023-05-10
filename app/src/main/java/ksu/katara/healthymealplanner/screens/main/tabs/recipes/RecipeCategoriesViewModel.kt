package ksu.katara.healthymealplanner.screens.main.tabs.recipes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ksu.katara.healthymealplanner.model.recipecategories.CategoriesRepository
import ksu.katara.healthymealplanner.model.recipecategories.RecipeCategoriesListener
import ksu.katara.healthymealplanner.model.recipecategories.entities.Category
import ksu.katara.healthymealplanner.screens.base.BaseViewModel
import ksu.katara.healthymealplanner.screens.base.Event
import ksu.katara.healthymealplanner.tasks.EmptyResult
import ksu.katara.healthymealplanner.tasks.ErrorResult
import ksu.katara.healthymealplanner.tasks.PendingResult
import ksu.katara.healthymealplanner.tasks.StatusResult
import ksu.katara.healthymealplanner.tasks.SuccessResult

data class RecipeCategoriesListItem(
    val recipeCategory: Category,
    val isInProgress: Boolean
)

class RecipeCategoriesViewModel(
    private val recipeCategoriesRepository: CategoriesRepository
) : BaseViewModel(), RecipeCategoryActionListener {

    private val _recipeCategories = MutableLiveData<StatusResult<List<RecipeCategoriesListItem>>>()
    val recipeCategories: LiveData<StatusResult<List<RecipeCategoriesListItem>>> = _recipeCategories

    private val _actionShowDetails = MutableLiveData<Event<Category>>()
    val actionShowDetails: LiveData<Event<Category>> = _actionShowDetails

    private val recipeCategoryIdsInProgress = mutableSetOf<Long>()
    private var recipeCategoriesResult: StatusResult<List<Category>> = EmptyResult()
        set(value) {
            field = value
            notifyUpdates()
        }

    private val listener: RecipeCategoriesListener = {
        recipeCategoriesResult = if (it.isEmpty()) {
            EmptyResult()
        } else {
            SuccessResult(it)
        }
    }

    init {
        recipeCategoriesRepository.addListener(listener)
        loadRecipeCategories()
    }

    private fun loadRecipeCategories() {
        recipeCategoriesResult = PendingResult()
        recipeCategoriesRepository.loadRecipeCategories()
            .onError {
                recipeCategoriesResult = ErrorResult(it)
            }
            .autoCancel()
    }

    override fun onCleared() {
        super.onCleared()
        recipeCategoriesRepository.removeListener(listener)
    }

    private fun isInProgress(recipeCategory: Category): Boolean {
        return recipeCategoryIdsInProgress.contains(recipeCategory.id)
    }

    private fun notifyUpdates() {
        _recipeCategories.postValue(recipeCategoriesResult.resultMap { recipeCategories ->
            recipeCategories.map { recipeCategory ->
                RecipeCategoriesListItem(recipeCategory, isInProgress(recipeCategory)) } })
    }

    override fun invoke(recipeCategory: Category) {
        _actionShowDetails.value = Event(recipeCategory)
    }
}