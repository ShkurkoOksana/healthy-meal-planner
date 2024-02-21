package ksu.katara.healthymealplanner.mvvm.presentation.main.tabs.recipecategories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.foundation.navigator.Navigator
import ksu.katara.healthymealplanner.foundation.uiactions.UiActions
import ksu.katara.healthymealplanner.foundation.views.BaseViewModel
import ksu.katara.healthymealplanner.foundation.views.LiveResult
import ksu.katara.healthymealplanner.foundation.views.MutableLiveResult
import ksu.katara.healthymealplanner.mvvm.domain.recipecategories.RecipeCategoriesRepository
import ksu.katara.healthymealplanner.mvvm.domain.recipecategories.entities.RecipeCategory
import ksu.katara.healthymealplanner.mvvm.presentation.main.tabs.recipecategories.recipesincategory.RecipesInCategoryFragment
import javax.inject.Inject

@HiltViewModel
class RecipeCategoriesViewModel @Inject constructor(
    private val navigator: Navigator,
    uiActions: UiActions,
    private val recipeCategoriesRepository: RecipeCategoriesRepository,
) : BaseViewModel(), RecipeCategoryActionListener {

    private val _recipeCategories = MutableLiveResult<List<RecipeCategory>>()
    val recipeCategories: LiveResult<List<RecipeCategory>> = _recipeCategories

    private val _screenTitle = MutableLiveData<String>()
    val screenTitle: LiveData<String> = _screenTitle

    init {
        _screenTitle.value = uiActions.getString(R.string.recipes_categories_title)
        loadRecipeCategories()
    }

    private fun loadRecipeCategories() = into(_recipeCategories) {
        recipeCategoriesRepository.loadRecipeCategories()
    }

    override fun onRecipeCategoryPressed(recipeCategory: RecipeCategory) {
        val screen = RecipesInCategoryFragment.Screen(recipeCategory)
        navigator.launch(
            R.id.recipesInCategoryFragment,
            RecipesInCategoryFragment.createArgs(screen)
        )
    }

    fun tryAgain() {
        loadRecipeCategories()
    }
}