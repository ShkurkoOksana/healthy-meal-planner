package ksu.katara.healthymealplanner.mvvm.screens.main.tabs.recipes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.mvvm.model.recipecategories.CategoriesRepository
import ksu.katara.healthymealplanner.mvvm.model.recipecategories.RecipeCategoriesListener
import ksu.katara.healthymealplanner.mvvm.model.recipecategories.entities.Category
import ksu.katara.healthymealplanner.mvvm.screens.base.BaseViewModel
import ksu.katara.healthymealplanner.mvvm.screens.base.Event
import ksu.katara.healthymealplanner.mvvm.tasks.EmptyResult
import ksu.katara.healthymealplanner.mvvm.tasks.PendingResult
import ksu.katara.healthymealplanner.mvvm.tasks.StatusResult
import ksu.katara.healthymealplanner.mvvm.tasks.SuccessResult

class RecipeCategoriesViewModel(
    private val recipeCategoriesRepository: CategoriesRepository
) : BaseViewModel(), RecipeCategoryActionListener {

    private val _recipeCategories = MutableLiveData<StatusResult<List<Category>>>()
    val recipeCategories: LiveData<StatusResult<List<Category>>> = _recipeCategories

    private val _actionShowToast = MutableLiveData<Event<Int>>()
    val actionShowToast: LiveData<Event<Int>> = _actionShowToast

    private val _actionShowDetails = MutableLiveData<Event<Category>>()
    val actionShowDetails: LiveData<Event<Category>> = _actionShowDetails

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
                _actionShowToast.value = Event(R.string.cant_load_recipe_categories)
            }
            .autoCancel()
    }

    override fun onCleared() {
        super.onCleared()
        recipeCategoriesRepository.removeListener(listener)
    }

    private fun notifyUpdates() {
        _recipeCategories.postValue(recipeCategoriesResult)
    }

    override fun invoke(recipeCategory: Category) {
        _actionShowDetails.value = Event(recipeCategory)
    }
}