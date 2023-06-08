package ksu.katara.healthymealplanner.screens.main.tabs.mealplan.mealplanfordate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.model.meal.enum.MealTypes
import ksu.katara.healthymealplanner.model.mealplan.MealPlanForDateRecipesListener
import ksu.katara.healthymealplanner.model.mealplan.MealPlanForDateRecipesRepository
import ksu.katara.healthymealplanner.model.mealplan.entities.MealPlanRecipes
import ksu.katara.healthymealplanner.model.recipes.entities.Recipe
import ksu.katara.healthymealplanner.screens.base.BaseViewModel
import ksu.katara.healthymealplanner.screens.base.Event
import ksu.katara.healthymealplanner.tasks.EmptyResult
import ksu.katara.healthymealplanner.tasks.PendingResult
import ksu.katara.healthymealplanner.tasks.StatusResult
import ksu.katara.healthymealplanner.tasks.SuccessResult

data class MealPlanForDateRecipesItem(
    val recipe: Recipe,
    val isInProgress: Boolean,
)

class MealPlanDateRecipesListViewModel(
    private val selectedDate: String,
    private val mealType: MealTypes,
    private val mealPlanForDateRecipesRepository: MealPlanForDateRecipesRepository,
) : BaseViewModel(), MealPlanDateRecipeActionListener {

    private val _mealPlanForDateRecipes = MutableLiveData<StatusResult<List<MealPlanForDateRecipesItem>>>()
    val mealPlanForDateRecipes: LiveData<StatusResult<List<MealPlanForDateRecipesItem>>> = _mealPlanForDateRecipes

    private val _actionShowToast = MutableLiveData<Event<Int>>()
    val actionShowToast: LiveData<Event<Int>> = _actionShowToast

    private val _actionShowDetails = MutableLiveData<Event<Recipe>>()
    val actionShowDetails: LiveData<Event<Recipe>> = _actionShowDetails

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

    init {
        mealPlanForDateRecipesRepository.addMealPlanForDateRecipesItemListener(mealPlanForDateRecipesListener)
        loadMealPlanForDateRecipes(selectedDate, mealType)
    }

    private fun loadMealPlanForDateRecipes(selectedDate: String, mealType: MealTypes) {
        mealPlanRecipesResult = PendingResult()
        mealPlanForDateRecipesRepository.loadMealPlanForDateRecipes(selectedDate, mealType)
            .onError {
                _actionShowToast.value = Event(R.string.cant_load_meal_plan_for_date_recipes)
            }
            .autoCancel()
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
                _actionShowToast.value = Event(R.string.cant_delete_recipe_from_meal_plan)
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
        _actionShowDetails.value = Event(recipe)
    }
}