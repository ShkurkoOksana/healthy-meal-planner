package ksu.katara.healthymealplanner.screens.main.tabs.home.mealplanfortoday.addrecipe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ksu.katara.healthymealplanner.model.addrecipephoto.AddRecipesPhotoListener
import ksu.katara.healthymealplanner.model.addrecipephoto.entities.AddRecipePhoto
import ksu.katara.healthymealplanner.model.addrecipephoto.entities.AddRecipesPhotoRepository
import ksu.katara.healthymealplanner.model.addrecipes.AddRecipesRepository
import ksu.katara.healthymealplanner.model.meal.enum.MealTypes
import ksu.katara.healthymealplanner.model.mealplanfortoday.MealPlanForTodayRecipesRepository
import ksu.katara.healthymealplanner.screens.base.BaseViewModel
import ksu.katara.healthymealplanner.tasks.EmptyResult
import ksu.katara.healthymealplanner.tasks.ErrorResult
import ksu.katara.healthymealplanner.tasks.PendingResult
import ksu.katara.healthymealplanner.tasks.StatusResult
import ksu.katara.healthymealplanner.tasks.SuccessResult

data class AddRecipesPhotoListItem(
    val addRecipePhoto: AddRecipePhoto,
    val isDeleteInProgress: Boolean,
)

class AddRecipesPhotoListViewModel(
    private val addRecipesRepository: AddRecipesRepository,
    private val addRecipesPhotoRepository: AddRecipesPhotoRepository,
    private val mealPlanForTodayRecipesRepository: MealPlanForTodayRecipesRepository,
) : BaseViewModel(), OnAddRecipesPhotoListItemDelete {

    private val _addRecipesPhotoList = MutableLiveData<StatusResult<List<AddRecipesPhotoListItem>>>()
    val addRecipesPhotoList: LiveData<StatusResult<List<AddRecipesPhotoListItem>>> = _addRecipesPhotoList

    private val addRecipesPhotoListDeleteItemIdsInProgress = mutableSetOf<Long>()

    private var addRecipesPhotoListResult: StatusResult<List<AddRecipePhoto>> = EmptyResult()
        set(value) {
            field = value
            notifyUpdates()
        }

    private val addRecipesPhotoListListener: AddRecipesPhotoListener = {
        addRecipesPhotoListResult = if (it.isEmpty()) {
            EmptyResult()
        } else {
            SuccessResult(it)
        }
    }

    init {
        addRecipesPhotoRepository.addAddRecipesPhotoListener(addRecipesPhotoListListener)
        loadAddRecipesPhoto()
    }

    private fun loadAddRecipesPhoto() {
        addRecipesPhotoListResult = PendingResult()
        addRecipesPhotoRepository.loadAddRecipesPhoto()
            .onError {
                addRecipesPhotoListResult = ErrorResult(it)
            }
            .autoCancel()
    }

    override fun onCleared() {
        super.onCleared()
        addRecipesPhotoRepository.removeAddRecipesPhotoListener(addRecipesPhotoListListener)
    }

    override fun invoke(mealTypes: MealTypes, addRecipePhoto: AddRecipePhoto) {
        if (isDeleteInProgress(addRecipePhoto)) return
        addDeleteProgressTo(addRecipePhoto)
        addRecipesPhotoRepository.addRecipesPhotoDeleteRecipe(addRecipePhoto)
            .onSuccess {
                removeDeleteProgressFrom(addRecipePhoto)
            }
            .autoCancel()

        mealPlanForTodayRecipesRepository.mealPlanForTodayRecipesDeleteRecipe(addRecipePhoto.id, mealTypes)
            .onSuccess {
            }
            .autoCancel()

        addRecipesRepository.addRecipesAddRecipe(addRecipePhoto.id)
            .onSuccess {
            }
            .autoCancel()
    }

    private fun addDeleteProgressTo(addRecipePhoto: AddRecipePhoto) {
        addRecipesPhotoListDeleteItemIdsInProgress.add(addRecipePhoto.id)
        notifyUpdates()
    }

    private fun removeDeleteProgressFrom(addRecipePhoto: AddRecipePhoto) {
        addRecipesPhotoListDeleteItemIdsInProgress.remove(addRecipePhoto.id)
        notifyUpdates()
    }

    private fun isDeleteInProgress(addRecipePhoto: AddRecipePhoto): Boolean {
        return addRecipesPhotoListDeleteItemIdsInProgress.contains(addRecipePhoto.id)
    }

    private fun notifyUpdates() {
        _addRecipesPhotoList.postValue(addRecipesPhotoListResult.resultMap { recipesPhotoList ->
            recipesPhotoList.map { addRecipePhoto -> AddRecipesPhotoListItem(addRecipePhoto, isDeleteInProgress(addRecipePhoto)) }
        })
    }
}