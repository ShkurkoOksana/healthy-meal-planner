package ksu.katara.healthymealplanner.mvvm.views.main.tabs.home

import android.os.Bundle
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
import ksu.katara.healthymealplanner.mvvm.model.dietTips.DietTipsListener
import ksu.katara.healthymealplanner.mvvm.model.dietTips.DietTipsRepository
import ksu.katara.healthymealplanner.mvvm.model.dietTips.entities.DietTip
import ksu.katara.healthymealplanner.mvvm.views.main.tabs.home.HomeFragment.Screen
import ksu.katara.healthymealplanner.mvvm.views.main.tabs.home.diettips.DietTipActionListener
import ksu.katara.healthymealplanner.mvvm.views.main.tabs.home.diettips.DietTipDetailsFragment
import ksu.katara.healthymealplanner.mvvm.views.main.tabs.mealplan.mealplanfordate.MealPlanForDateRecipesFragment
import java.util.Date

class HomeViewModel(
    private val screen: Screen,
    private val navigator: Navigator,
    private val uiActions: UiActions,
    private val dietTipsRepository: DietTipsRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(), DietTipActionListener {

    private val _dietTips = MutableLiveData<StatusResult<List<DietTip>>>()
    val dietTips: LiveData<StatusResult<List<DietTip>>> = _dietTips

    private val _screenTitle = MutableLiveData<String>()
    val screenTitle: LiveData<String> = _screenTitle

    private var dietTipsResult: StatusResult<List<DietTip>> = EmptyResult()
        set(value) {
            field = value
            notifyUpdates()
        }

    private val listener: DietTipsListener = {
        dietTipsResult = if (it.isEmpty()) {
            EmptyResult()
        } else {
            SuccessResult(it)
        }
    }

    init {
        _screenTitle.value = uiActions.getString(R.string.home_title)
        dietTipsRepository.addDietTipsListener(listener)
        loadDietTips()
    }

    private fun loadDietTips() {
        dietTipsResult = PendingResult()
        dietTipsRepository.loadDietTips()
            .onError {
                val message = uiActions.getString(R.string.cant_load_diet_tips_chapters)
                uiActions.toast(message)
            }
            .autoCancel()
    }

    fun onMorePressed(destinationId: Int, args: Bundle?) {
        navigator.launch(destinationId, args)
    }

    fun onMealPlanForDateItemPressed(destinationId: Int, currentDate: Date, mealTypes: MealTypes) {
        val screen = MealPlanForDateRecipesFragment.Screen(currentDate, mealTypes)
        navigator.launch(destinationId, MealPlanForDateRecipesFragment.createArgs(screen))
    }

    override fun onCleared() {
        super.onCleared()
        dietTipsRepository.removeDietTipsListener(listener)
    }

    private fun notifyUpdates() {
        _dietTips.postValue(dietTipsResult)
    }

    override fun onDietTipPressed(dietTipId: Long) {
        val screen = DietTipDetailsFragment.Screen(dietTipId)
        navigator.launch(R.id.dietTipDetailsFragment, DietTipDetailsFragment.createArgs(screen))
    }
}