package ksu.katara.healthymealplanner.screens.main.tabs.home.mealplanfortoday

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ksu.katara.healthymealplanner.model.addrecipes.AddRecipesRepository
import ksu.katara.healthymealplanner.model.meal.enum.MealTypes
import ksu.katara.healthymealplanner.model.mealplanfortoday.MealPlanForTodayRecipesListener
import ksu.katara.healthymealplanner.model.mealplanfortoday.MealPlanForTodayRecipesRepository
import ksu.katara.healthymealplanner.model.mealplanfortoday.entities.MealPlanForTodayRecipes
import ksu.katara.healthymealplanner.model.recipes.entities.Recipe
import ksu.katara.healthymealplanner.screens.base.BaseViewModel
import ksu.katara.healthymealplanner.screens.base.Event
import ksu.katara.healthymealplanner.tasks.EmptyResult
import ksu.katara.healthymealplanner.tasks.ErrorResult
import ksu.katara.healthymealplanner.tasks.PendingResult
import ksu.katara.healthymealplanner.tasks.StatusResult
import ksu.katara.healthymealplanner.tasks.SuccessResult

data class MealPlanForTodayRecipesItem(
    val recipe: Recipe,
    val isInProgress: Boolean,
)

class MealPlanForTodayRecipesListViewModel(
    private val mealType: MealTypes,
    private val mealPlanForTodayRecipesRepository: MealPlanForTodayRecipesRepository,
    private val addRecipesRepository: AddRecipesRepository,
) : BaseViewModel(), MealPlanForTodayRecipeActionListener {

    private val _mealPlanForTodayRecipes = MutableLiveData<StatusResult<List<MealPlanForTodayRecipesItem>>>()
    val mealPlanForTodayRecipes: LiveData<StatusResult<List<MealPlanForTodayRecipesItem>>> = _mealPlanForTodayRecipes

    private val _actionShowDetails = MutableLiveData<Event<Recipe>>()
    val actionShowDetails: LiveData<Event<Recipe>> = _actionShowDetails

    private val mealPlanForTodayRecipesDeleteItemIdsInProgress = mutableSetOf<Long>()
    private var mealPlanForTodayRecipesResult: StatusResult<MealPlanForTodayRecipes> = EmptyResult()
        set(value) {
            field = value
            notifyUpdates()
        }

    private val mealPlanForTodayRecipesListener: MealPlanForTodayRecipesListener = {
        mealPlanForTodayRecipesResult = if (it.recipesList.isEmpty()) {
            EmptyResult()
        } else {
            SuccessResult(it)
        }
    }

    init {
        mealPlanForTodayRecipesRepository.addMealPlanForTodayRecipesItemListener(mealPlanForTodayRecipesListener)
        loadMealPlanForTodayRecipes(mealType)
    }

    private fun loadMealPlanForTodayRecipes(mealType: MealTypes) {
        mealPlanForTodayRecipesResult = PendingResult()
        mealPlanForTodayRecipesRepository.loadMealPlanForTodayRecipes(mealType)
            .onError {
                mealPlanForTodayRecipesResult = ErrorResult(it)
            }
            .autoCancel()
    }

    override fun onCleared() {
        super.onCleared()
        mealPlanForTodayRecipesRepository.removeMealPlanForTodayRecipesItemListener(mealPlanForTodayRecipesListener)
    }

    override fun onMealPlanForTodayRecipesItemDelete(recipe: Recipe) {
        if (isDeleteInProgress(recipe)) return
        addDeleteProgressTo(recipe)
        mealPlanForTodayRecipesRepository.mealPlanForTodayRecipesDeleteRecipe(recipe.id, mealType)
            .onSuccess {
                removeDeleteProgressFrom(recipe)
            }
            .autoCancel()

        addRecipesRepository.addRecipesAddRecipe(recipe.id)
            .onSuccess {
            }
            .autoCancel()
    }

    override fun onMealPlanForTodayRecipesItemDetails(recipe: Recipe) {
        _actionShowDetails.value = Event(recipe)
    }

    private fun addDeleteProgressTo(recipe: Recipe) {
        mealPlanForTodayRecipesDeleteItemIdsInProgress.add(recipe.id)
        notifyUpdates()
    }

    private fun removeDeleteProgressFrom(recipe: Recipe) {
        mealPlanForTodayRecipesDeleteItemIdsInProgress.remove(recipe.id)
        notifyUpdates()
    }

    private fun isDeleteInProgress(recipe: Recipe): Boolean {
        return mealPlanForTodayRecipesDeleteItemIdsInProgress.contains(recipe.id)
    }

    private fun notifyUpdates() {
        _mealPlanForTodayRecipes.postValue(mealPlanForTodayRecipesResult.resultMap { mealPlanForTodayRecipes ->
            mealPlanForTodayRecipes.recipesList.map { recipe -> MealPlanForTodayRecipesItem(recipe, isDeleteInProgress(recipe)) }
        })
    }
}