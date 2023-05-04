package ksu.katara.healthymealplanner.screens.main.tabs.home.mealplanfortoday.addrecipe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ksu.katara.healthymealplanner.model.addrecipephoto.entities.AddRecipesPhotoRepository
import ksu.katara.healthymealplanner.model.addrecipes.AddRecipesRepository
import ksu.katara.healthymealplanner.model.meal.enum.MealTypes
import ksu.katara.healthymealplanner.model.mealplanfortoday.MealPlanForTodayRecipesRepository
import ksu.katara.healthymealplanner.model.recipes.RecipesListener
import ksu.katara.healthymealplanner.model.recipes.entities.Recipe
import ksu.katara.healthymealplanner.screens.base.BaseViewModel
import ksu.katara.healthymealplanner.tasks.EmptyResult
import ksu.katara.healthymealplanner.tasks.ErrorResult
import ksu.katara.healthymealplanner.tasks.PendingResult
import ksu.katara.healthymealplanner.tasks.StatusResult
import ksu.katara.healthymealplanner.tasks.SuccessResult

data class AddRecipesListItem(
    val recipe: Recipe,
    val isDeleteInProgress: Boolean
)

class AddRecipesListViewModel(
    mealType: MealTypes,
    private val addRecipesRepository: AddRecipesRepository,
    private val addRecipesPhotoRepository: AddRecipesPhotoRepository,
    private val mealPlanForTodayRecipesRepository: MealPlanForTodayRecipesRepository
) : BaseViewModel(), OnAddRecipesListItemDelete {

    private val _addRecipesList = MutableLiveData<StatusResult<MutableList<AddRecipesListItem>>>()
    val addRecipesList: LiveData<StatusResult<MutableList<AddRecipesListItem>>> = _addRecipesList

    private val addRecipesListDeleteItemIdsInProgress = mutableSetOf<Long>()

    private var addRecipesListResult: StatusResult<List<Recipe>> = EmptyResult()
        set(value) {
            field = value
            notifyUpdates()
        }

    private val addRecipesListListener: RecipesListener = {
        addRecipesListResult = if (it.isEmpty()) {
            EmptyResult()
        } else {
            SuccessResult(it)
        }
    }

    init {
        addRecipesRepository.addAddRecipesListener(addRecipesListListener)
        loadAddRecipes(mealType)
    }

    private fun loadAddRecipes(mealType: MealTypes) {
        addRecipesListResult = PendingResult()
        addRecipesRepository.loadAddRecipes(mealType)
            .onError {
                addRecipesListResult = ErrorResult(it)
            }
            .autoCancel()
    }

    override fun onCleared() {
        super.onCleared()
        addRecipesRepository.removeAddRecipesListener(addRecipesListListener)
    }

    override fun invoke(mealType: MealTypes, recipe: Recipe) {
        if (isDeleteInProgress(recipe)) return
        addDeleteProgressTo(recipe)
        mealPlanForTodayRecipesRepository.mealPlanForTodayRecipesAddRecipe(recipe, mealType)
            .onSuccess {
            }
            .autoCancel()


        addRecipesPhotoRepository.addRecipesPhotoAddRecipe(recipe.id)
            .onSuccess {
            }
            .autoCancel()

        addRecipesRepository.addRecipesDeleteRecipe(recipe)
            .onSuccess {
                removeDeleteProgressFrom(recipe)
            }
            .autoCancel()
    }

    private fun addDeleteProgressTo(recipe: Recipe) {
        addRecipesListDeleteItemIdsInProgress.add(recipe.id)
        notifyUpdates()
    }

    private fun removeDeleteProgressFrom(recipe: Recipe) {
        addRecipesListDeleteItemIdsInProgress.remove(recipe.id)
        notifyUpdates()
    }

    private fun isDeleteInProgress(recipe: Recipe): Boolean {
        return addRecipesListDeleteItemIdsInProgress.contains(recipe.id)
    }

    private fun notifyUpdates() {
        _addRecipesList.postValue(addRecipesListResult.resultMap { recipesList ->
            recipesList.map { recipe -> AddRecipesListItem(recipe, isDeleteInProgress(recipe)) }
        } as StatusResult<MutableList<AddRecipesListItem>>)
    }
}