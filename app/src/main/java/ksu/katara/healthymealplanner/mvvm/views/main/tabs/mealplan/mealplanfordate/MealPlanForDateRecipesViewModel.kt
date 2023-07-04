package ksu.katara.healthymealplanner.mvvm.views.main.tabs.mealplan.mealplanfordate

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
import ksu.katara.healthymealplanner.mvvm.model.mealplan.MealPlanForDateRecipesListener
import ksu.katara.healthymealplanner.mvvm.model.mealplan.MealPlanForDateRecipesRepository
import ksu.katara.healthymealplanner.mvvm.model.mealplan.entities.MealPlanRecipes
import ksu.katara.healthymealplanner.mvvm.model.recipes.entities.Recipe
import ksu.katara.healthymealplanner.mvvm.views.main.tabs.home.MealTypes
import ksu.katara.healthymealplanner.mvvm.views.main.tabs.home.recipedetails.RecipeDetailsFragment
import ksu.katara.healthymealplanner.mvvm.views.main.tabs.home.sdf
import ksu.katara.healthymealplanner.mvvm.views.main.tabs.mealplan.addrecipes.AddRecipesFragment
import ksu.katara.healthymealplanner.mvvm.views.main.tabs.mealplan.mealplanfordate.MealPlanForDateRecipesFragment.Screen
import java.util.Date

data class MealPlanForDateRecipesItem(
    val recipe: Recipe,
    val isInProgress: Boolean,
)

class MealPlanForDateRecipesViewModel(
    screen: Screen,
    private val navigator: Navigator,
    private val uiActions: UiActions,
    private val mealPlanForDateRecipesRepository: MealPlanForDateRecipesRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(), MealPlanDateRecipeActionListener {
    private val _screenTitle = MutableLiveData<String>()
    val screenTitle: LiveData<String> = _screenTitle

    private val _mealPlanForDateRecipes = MutableLiveData<StatusResult<List<MealPlanForDateRecipesItem>>>()
    val mealPlanForDateRecipes: LiveData<StatusResult<List<MealPlanForDateRecipesItem>>> = _mealPlanForDateRecipes

    private val mealPlanForDateRecipesDeleteItemIdsInProgress = mutableSetOf<Long>()
    private var mealPlanRecipesResult: StatusResult<MealPlanRecipes> = EmptyResult()
        set(value) {
            field = value
            notifyUpdates()
        }

    private val mealPlanForDateRecipesListener: MealPlanForDateRecipesListener = {
        mealPlanRecipesResult = if (it == null) {
            EmptyResult()
        } else {
            SuccessResult(it)
        }
    }

    private val selectedDate = screen.selectedDate
    private val mealType = screen.mealType

    init {
        _screenTitle.value = uiActions.getString(R.string.meal_plan_for_date_title, mealType.mealName , sdf.format(selectedDate))
        mealPlanForDateRecipesRepository.addMealPlanForDateRecipesItemListener(mealPlanForDateRecipesListener)
        loadMealPlanForDateRecipes(selectedDate, mealType)
    }

    private fun loadMealPlanForDateRecipes(selectedDate: Date, mealType: MealTypes) {
        mealPlanRecipesResult = PendingResult()
        mealPlanForDateRecipesRepository.loadMealPlanForDateRecipes(selectedDate, mealType)
            .onError {
                val message = uiActions.getString(R.string.cant_load_meal_plan_for_date_recipes)
                uiActions.toast(message)
            }
            .autoCancel()
    }

    fun onMealPlanForDateAddButtonPressed() {
        val screen = AddRecipesFragment.Screen(selectedDate, mealType)
        navigator.launch(R.id.addRecipesFragment, AddRecipesFragment.createArgs(screen))
    }

    override fun onCleared() {
        super.onCleared()
        mealPlanForDateRecipesRepository.removeMealPlanForDateRecipesItemListener(mealPlanForDateRecipesListener)
    }

    override fun onMealPlanForDateRecipesItemDelete(recipe: Recipe) {
        if (isDeleteInProgress(recipe)) return
        addDeleteProgressTo(recipe)
        mealPlanForDateRecipesRepository.mealPlanForDateRecipesDeleteRecipe(selectedDate, mealType, recipe)
            .onSuccess {
                removeDeleteProgressFrom(recipe)
                if (it == null) {
                    mealPlanRecipesResult = EmptyResult()
                }
            }
            .onError {
                val message = uiActions.getString(R.string.cant_delete_recipe_from_meal_plan)
                uiActions.toast(message)
            }
            .autoCancel()
    }

    private fun addDeleteProgressTo(recipe: Recipe) {
        mealPlanForDateRecipesDeleteItemIdsInProgress.add(recipe.id)
        notifyUpdates()
    }

    private fun removeDeleteProgressFrom(recipe: Recipe) {
        mealPlanForDateRecipesDeleteItemIdsInProgress.remove(recipe.id)
        notifyUpdates()
    }

    private fun isDeleteInProgress(recipe: Recipe): Boolean {
        return mealPlanForDateRecipesDeleteItemIdsInProgress.contains(recipe.id)
    }

    private fun notifyUpdates() {
        _mealPlanForDateRecipes.postValue(mealPlanRecipesResult.resultMap { mealPlanForDateRecipes ->
            mealPlanForDateRecipes.recipesList.map { recipe -> MealPlanForDateRecipesItem(recipe, isDeleteInProgress(recipe)) }
        })
    }

    override fun onMealPlanForDateRecipesItemDetails(recipe: Recipe) {
        val screen = RecipeDetailsFragment.Screen(recipe)
        navigator.launch(R.id.recipeDetailsFragment, RecipeDetailsFragment.createArgs(screen))
    }
}