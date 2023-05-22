package ksu.katara.healthymealplanner.screens.main.tabs.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ksu.katara.healthymealplanner.model.mealplan.MealPlanForDateRecipesRepository
import ksu.katara.healthymealplanner.screens.base.BaseViewModel

class MealPlanViewModel(
    private val mealPlanForDateRecipesRepository: MealPlanForDateRecipesRepository,
) : BaseViewModel() {
    private val _isMealPlanLoaded = MutableLiveData<Boolean>()
    val isMealPlanLoaded: LiveData<Boolean> = _isMealPlanLoaded

    init {
        initMealPlanForDate()
    }

    private fun initMealPlanForDate() {
        mealPlanForDateRecipesRepository.initMealPlan()
            .onSuccess {
                _isMealPlanLoaded.value = true
            }
            .onError {
                _isMealPlanLoaded.value = false
            }
            .autoCancel()
    }
}