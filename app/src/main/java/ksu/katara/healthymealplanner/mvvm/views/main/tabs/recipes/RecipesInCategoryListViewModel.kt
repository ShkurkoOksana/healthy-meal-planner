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
import ksu.katara.healthymealplanner.mvvm.model.recipes.RecipesInCategoryListener
import ksu.katara.healthymealplanner.mvvm.model.recipes.RecipesRepository
import ksu.katara.healthymealplanner.mvvm.model.recipes.entities.Recipe
import ksu.katara.healthymealplanner.mvvm.views.main.tabs.home.recipedetails.RecipeDetailsFragment
import ksu.katara.healthymealplanner.mvvm.views.main.tabs.recipes.RecipesInCategoryFragment.Screen

class RecipesInCategoryViewModel(
    screen: Screen,
    private val navigator: Navigator,
    private val uiActions: UiActions,
    private val recipesRepository: RecipesRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(), RecipesInCategoryActionListener {

    private val _screenTitle = MutableLiveData<String>()
    val screenTitle: LiveData<String> = _screenTitle

    private val _recipesInCategory = MutableLiveData<StatusResult<List<Recipe>>>()
    val recipesInCategory: LiveData<StatusResult<List<Recipe>>> = _recipesInCategory

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

    private val recipeCategoryId = screen.recipeCategory.id
    private val recipeCategoryName = screen.recipeCategory.name

    init {
        _screenTitle.value = uiActions.getString(R.string.recipe_in_category_title, recipeCategoryName)
        recipesRepository.addRecipeInCategoryListener(listener)
        loadRecipesInCategory(recipeCategoryId)
    }

    private fun loadRecipesInCategory(recipeCategoryId: Long) {
        recipesInCategoryResult = PendingResult()
        recipesRepository.loadRecipesInCategory(recipeCategoryId)
            .onError {
                val message = uiActions.getString(R.string.cant_load_recipe_in_category)
                uiActions.toast(message)
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

    override fun onRecipeInCategoryPressed(recipe: Recipe) {
        val screen = RecipeDetailsFragment.Screen(recipe)
        navigator.launch(R.id.recipeDetailsFragment, RecipeDetailsFragment.createArgs(screen))
    }
}