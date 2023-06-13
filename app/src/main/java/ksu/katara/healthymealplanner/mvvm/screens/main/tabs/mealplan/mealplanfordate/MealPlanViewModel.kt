package ksu.katara.healthymealplanner.mvvm.screens.main.tabs.mealplan.mealplanfordate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.mvvm.model.mealplan.MealPlanForDateRecipesRepository
import ksu.katara.healthymealplanner.mvvm.screens.base.BaseViewModel
import ksu.katara.healthymealplanner.mvvm.screens.base.Event

class MealPlanViewModel(
    private val mealPlanForDateRecipesRepository: MealPlanForDateRecipesRepository,
) : BaseViewModel() {
    private val _isMealPlanLoaded = MutableLiveData<Boolean>()
    val isMealPlanLoaded: LiveData<Boolean> = _isMealPlanLoaded

    private val _actionShowToast = MutableLiveData<Event<Int>>()
    val actionShowToast: LiveData<Event<Int>> = _actionShowToast

    init {
        initMealPlanForDate()
    }

    private fun initMealPlanForDate() {
        mealPlanForDateRecipesRepository.loadMealPlan()
            .onSuccess {
                _isMealPlanLoaded.value = true
            }
            .onError {
                _isMealPlanLoaded.value = false
                _actionShowToast.value = Event(R.string.cant_load_meal_plan_for_date_recipes)
            }
            .autoCancel()
    }
}