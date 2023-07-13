package ksu.katara.healthymealplanner.mvvm.views.main.tabs.home

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.foundation.navigator.Navigator
import ksu.katara.healthymealplanner.foundation.uiactions.UiActions
import ksu.katara.healthymealplanner.foundation.views.BaseViewModel
import ksu.katara.healthymealplanner.foundation.views.LiveResult
import ksu.katara.healthymealplanner.foundation.views.MutableLiveResult
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
    savedStateHandle: SavedStateHandle,
) : BaseViewModel(), DietTipActionListener {

    private val _dietTips = MutableLiveResult<List<DietTip>>()
    val dietTips: LiveResult<List<DietTip>> = _dietTips

    private val _screenTitle = MutableLiveData<String>()
    val screenTitle: LiveData<String> = _screenTitle
    init {
        _screenTitle.value = uiActions.getString(R.string.home_title)
        loadDietTips()
    }

    private fun loadDietTips() = into(_dietTips) { dietTipsRepository.loadDietTips() }

    fun onMorePressed(destinationId: Int, args: Bundle?) {
        navigator.launch(destinationId, args)
    }

    fun onMealPlanForDateItemPressed(destinationId: Int, currentDate: Date, mealTypes: MealTypes) {
        val screen = MealPlanForDateRecipesFragment.Screen(currentDate, mealTypes)
        navigator.launch(destinationId, MealPlanForDateRecipesFragment.createArgs(screen))
    }

    override fun onDietTipPressed(dietTipId: Long) {
        val screen = DietTipDetailsFragment.Screen(dietTipId)
        navigator.launch(R.id.dietTipDetailsFragment, DietTipDetailsFragment.createArgs(screen))
    }

    fun tryAgain() {
        loadDietTips()
    }
}