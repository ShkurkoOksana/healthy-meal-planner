package ksu.katara.healthymealplanner.mvvm.presentation.main.tabs.recipecategories.recipesincategory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.foundation.navigator.Navigator
import ksu.katara.healthymealplanner.foundation.uiactions.UiActions
import ksu.katara.healthymealplanner.foundation.views.BaseScreen
import ksu.katara.healthymealplanner.foundation.views.BaseViewModel
import ksu.katara.healthymealplanner.foundation.views.LiveResult
import ksu.katara.healthymealplanner.foundation.views.MutableLiveResult
import ksu.katara.healthymealplanner.mvvm.domain.recipes.RecipesRepository
import ksu.katara.healthymealplanner.mvvm.domain.recipes.entities.Recipe
import ksu.katara.healthymealplanner.mvvm.presentation.main.tabs.recipecategories.recipedetails.RecipeDetailsFragment
import ksu.katara.healthymealplanner.mvvm.presentation.main.tabs.recipecategories.recipesincategory.RecipesInCategoryFragment.Screen

class RecipesInCategoryViewModel @AssistedInject constructor(
    @Assisted screen: BaseScreen,
    @Assisted private val navigator: Navigator,
    uiActions: UiActions,
    private val recipesRepository: RecipesRepository,
) : BaseViewModel(), RecipesInCategoryActionListener {

    private val _recipesInCategory = MutableLiveResult<List<Recipe>>()
    val recipesInCategory: LiveResult<List<Recipe>> = _recipesInCategory

    private val _screenTitle = MutableLiveData<String>()
    val screenTitle: LiveData<String> = _screenTitle

    private val recipeCategoryId = (screen as Screen).recipeCategory.id
    private val recipeCategoryName = (screen as Screen).recipeCategory.name

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

    @AssistedFactory
    interface Factory {
        fun create(screen: BaseScreen, navigator: Navigator): RecipesInCategoryViewModel
    }
}