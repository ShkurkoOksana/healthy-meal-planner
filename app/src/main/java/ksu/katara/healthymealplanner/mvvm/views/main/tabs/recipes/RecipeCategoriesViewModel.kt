package ksu.katara.healthymealplanner.mvvm.views.main.tabs.recipes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.foundation.model.EmptyResult
import ksu.katara.healthymealplanner.foundation.model.PendingResult
import ksu.katara.healthymealplanner.foundation.model.StatusResult
import ksu.katara.healthymealplanner.foundation.model.SuccessResult
import ksu.katara.healthymealplanner.foundation.navigator.Navigator
import ksu.katara.healthymealplanner.foundation.uiactions.UiActions
import ksu.katara.healthymealplanner.foundation.views.BaseViewModel
import ksu.katara.healthymealplanner.foundation.views.LiveResult
import ksu.katara.healthymealplanner.foundation.views.MutableLiveResult
import ksu.katara.healthymealplanner.mvvm.model.recipecategories.CategoriesRepository
import ksu.katara.healthymealplanner.mvvm.model.recipecategories.RecipeCategoriesListener
import ksu.katara.healthymealplanner.mvvm.model.recipecategories.entities.Category
import ksu.katara.healthymealplanner.mvvm.views.main.tabs.recipes.RecipeCategoriesFragment.Screen

class RecipeCategoriesViewModel(
    screen: Screen,
    private val navigator: Navigator,
    private val uiActions: UiActions,
    private val recipeCategoriesRepository: CategoriesRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(), RecipeCategoryActionListener {

    private val _recipeCategories = MutableLiveResult<List<Category>>()
    val recipeCategories: LiveResult<List<Category>> = _recipeCategories

    private val _screenTitle = MutableLiveData<String>()
    val screenTitle: LiveData<String> = _screenTitle

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
        _screenTitle.value = uiActions.getString(R.string.recipes_categories_title)
        recipeCategoriesRepository.addListener(listener)
        loadRecipeCategories()
    }

    private fun loadRecipeCategories() {
        recipeCategoriesResult = PendingResult()
        recipeCategoriesRepository.loadRecipeCategories()
            .onError {
                val message = uiActions.getString(R.string.cant_load_recipe_categories)
                uiActions.toast(message)
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

    override fun onRecipeCategoryPressed(recipeCategory: Category) {
        val screen = RecipesInCategoryFragment.Screen(recipeCategory)
        navigator.launch(R.id.recipesInCategoryFragment, RecipesInCategoryFragment.createArgs(screen))
    }
}