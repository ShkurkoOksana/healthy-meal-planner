package ksu.katara.healthymealplanner.screens.main.tabs.home.recipe.preparationsteps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ksu.katara.healthymealplanner.model.recipes.RecipesRepository
import ksu.katara.healthymealplanner.model.recipes.entities.RecipePreparationStep
import ksu.katara.healthymealplanner.screens.base.BaseViewModel
import ksu.katara.healthymealplanner.tasks.EmptyResult
import ksu.katara.healthymealplanner.tasks.ErrorResult
import ksu.katara.healthymealplanner.tasks.PendingResult
import ksu.katara.healthymealplanner.tasks.StatusResult
import ksu.katara.healthymealplanner.tasks.SuccessResult

class RecipePreparationStepsListViewModel(
    private val recipeId: Long,
    private val recipesRepository: RecipesRepository,
) : BaseViewModel() {

    private val _preparationSteps = MutableLiveData<StatusResult<List<RecipePreparationStep>>>()
    val preparationSteps: LiveData<StatusResult<List<RecipePreparationStep>>> = _preparationSteps

    private var preparationStepsResult: StatusResult<List<RecipePreparationStep>> = EmptyResult()
        set(value) {
            field = value
            notifyUpdates()
        }

    init {
        loadPreparationSteps()
    }

    private fun loadPreparationSteps() {
        preparationStepsResult = PendingResult()
        recipesRepository.loadPreparationSteps(recipeId)
            .onSuccess {
                _preparationSteps.value = SuccessResult(it)
            }
            .onError {
                preparationStepsResult = ErrorResult(it)
            }
            .autoCancel()
    }

    private fun notifyUpdates() {
        _preparationSteps.postValue(preparationStepsResult)
    }
}
