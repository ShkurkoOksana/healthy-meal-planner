package ksu.katara.healthymealplanner.mvvm.views.main.tabs.mealplan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.foundation.navigator.Navigator
import ksu.katara.healthymealplanner.foundation.uiactions.UiActions
import ksu.katara.healthymealplanner.foundation.views.BaseViewModel
import ksu.katara.healthymealplanner.mvvm.model.calendar.CalendarListener
import ksu.katara.healthymealplanner.mvvm.model.calendar.CalendarRepository
import ksu.katara.healthymealplanner.mvvm.views.main.tabs.home.MealTypes
import ksu.katara.healthymealplanner.mvvm.views.main.tabs.mealplan.mealplanfordate.MealPlanForDateRecipesFragment
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class MealPlanViewModel @Inject constructor(
    private val navigator: Navigator,
    uiActions: UiActions,
    private val calendarRepository: CalendarRepository,
) : BaseViewModel(), OnCalendarItemClickListener {
    private val _daysInMonth = MutableLiveData<MutableList<Date>>()
    val daysInMonth: LiveData<MutableList<Date>> = _daysInMonth

    private val _selectedData = MutableLiveData<Date>()
    val selectedData: LiveData<Date> = _selectedData

    private val _screenTitle = MutableLiveData<String>()
    val screenTitle: LiveData<String> = _screenTitle

    private var calendar: List<Date> = listOf()

    private val calendarListener: CalendarListener = {
        calendar = it.ifEmpty {
            listOf()
        }
    }

    init {
        _screenTitle.value = uiActions.getString(R.string.meal_plan_title)
        loadCalendar()
    }

    private fun loadCalendar() {
        val currentDate = Date()
        _selectedData.value = currentDate
        _daysInMonth.value = calendarRepository.getCalendar()
    }

    fun onMealPlanForSelectedDateItemPressed(selectedDate: Date, mealType: MealTypes) {
        val screen = MealPlanForDateRecipesFragment.Screen(selectedDate, mealType)
        navigator.launch(
            R.id.mealPlanForDateRecipesFragment,
            MealPlanForDateRecipesFragment.createArgs(screen)
        )
    }

    override fun onCleared() {
        super.onCleared()
        calendarRepository.removeListener(calendarListener)
    }

    override fun invoke(position: Int) {
        _selectedData.value = _daysInMonth.value!![position]
    }
}