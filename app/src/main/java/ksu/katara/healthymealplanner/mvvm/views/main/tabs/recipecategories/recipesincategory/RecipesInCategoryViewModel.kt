package ksu.katara.healthymealplanner.mvvm.views.main.tabs.recipecategories.recipesincategory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.foundation.navigator.Navigator
import ksu.katara.healthymealplanner.foundation.uiactions.UiActions
import ksu.katara.healthymealplanner.foundation.views.BaseViewModel
import ksu.katara.healthymealplanner.foundation.views.LiveResult
import ksu.katara.healthymealplanner.foundation.views.MutableLiveResult
import ksu.katara.healthymealplanner.mvvm.model.recipes.RecipesRepository
import ksu.katara.healthymealplanner.mvvm.model.recipes.entities.Recipe
import ksu.katara.healthymealplanner.mvvm.views.main.tabs.recipecategories.recipedetails.RecipeDetailsFragment
import ksu.katara.healthymealplanner.mvvm.views.main.tabs.recipecategories.recipesincategory.RecipesInCategoryFragment.Screen

class RecipesInCategoryViewModel(
    screen: Screen,
    private val navigator: Navigator,
    private val uiActions: UiActions,
    private val recipesRepository: RecipesRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel(), RecipesInCategoryActionListener {

    private val _recipesInCategory = MutableLiveResult<List<Recipe>>()
    val recipesInCategory: LiveResult<List<Recipe>> = _recipesInCategory

    private val _screenTitle = MutableLiveData<String>()
    val screenTitle: LiveData<String> = _screenTitle

    private val recipeCategoryId = screen.recipeCategory.id
    private val recipeCategoryName = screen.recipeCategory.name

    init {
        _screenTitle.value =
            uiActions.getString(R.string.recipe_in_category_title, recipeCategoryName)
        loadRecipesInCategory(recipeCategoryId)
    }

    private fun loadRecipesInCategory(recipeCategoryId: Long) = into(_recipesInCategory) {
        recipesRepository.loadRecipesInCategory(recipeCategoryId)
    }

    override fun onRecipeInCategoryPressed(recipe: Recipe) {
        val screen = RecipeDetailsFragment.Screen(recipe)
        navigator.launch(R.id.recipeDetailsFragment, RecipeDetailsFragment.createArgs(screen))
    }

    fun tryAgain() {
        loadRecipesInCategory(recipeCategoryId)
    }
}