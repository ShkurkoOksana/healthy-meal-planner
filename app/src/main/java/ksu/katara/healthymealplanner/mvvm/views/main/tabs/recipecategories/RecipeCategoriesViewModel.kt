package ksu.katara.healthymealplanner.mvvm.views.main.tabs.recipecategories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.foundation.navigator.Navigator
import ksu.katara.healthymealplanner.foundation.uiactions.UiActions
import ksu.katara.healthymealplanner.foundation.views.BaseViewModel
import ksu.katara.healthymealplanner.foundation.views.LiveResult
import ksu.katara.healthymealplanner.foundation.views.MutableLiveResult
import ksu.katara.healthymealplanner.mvvm.model.recipecategories.CategoriesRepository
import ksu.katara.healthymealplanner.mvvm.model.recipecategories.entities.Category
import ksu.katara.healthymealplanner.mvvm.views.main.tabs.recipecategories.recipesincategory.RecipesInCategoryFragment
import ksu.katara.healthymealplanner.mvvm.views.main.tabs.recipecategories.RecipeCategoriesFragment.Screen

class RecipeCategoriesViewModel(
    screen: Screen,
    private val navigator: Navigator,
    private val uiActions: UiActions,
    private val recipeCategoriesRepository: CategoriesRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel(), RecipeCategoryActionListener {

    private val _recipeCategories = MutableLiveResult<List<Category>>()
    val recipeCategories: LiveResult<List<Category>> = _recipeCategories

    private val _screenTitle = MutableLiveData<String>()
    val screenTitle: LiveData<String> = _screenTitle

    init {
        _screenTitle.value = uiActions.getString(R.string.recipes_categories_title)
        loadRecipeCategories()
    }

    private fun loadRecipeCategories() = into(_recipeCategories) {
        recipeCategoriesRepository.load()
    }

    override fun onRecipeCategoryPressed(recipeCategory: Category) {
        val screen = RecipesInCategoryFragment.Screen(recipeCategory)
        navigator.launch(R.id.recipesInCategoryFragment, RecipesInCategoryFragment.createArgs(screen))
    }

    fun tryAgain() {
        loadRecipeCategories()
    }
}