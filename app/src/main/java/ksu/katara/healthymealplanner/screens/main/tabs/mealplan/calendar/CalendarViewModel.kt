package ksu.katara.healthymealplanner.screens.main.tabs.mealplan.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ksu.katara.healthymealplanner.model.calendar.CalendarListener
import ksu.katara.healthymealplanner.model.calendar.CalendarRepository
import ksu.katara.healthymealplanner.screens.base.BaseViewModel
import java.util.Date

class CalendarViewModel(
    private val calendarRepository: CalendarRepository,
) : BaseViewModel(), OnCalendarItemClickListener {
    private val _daysInMonth = MutableLiveData<MutableList<Date>>()
    val daysInMonth: LiveData<MutableList<Date>> = _daysInMonth

    private val _selectedData = MutableLiveData<Date>()
    val selectedData: LiveData<Date> = _selectedData

    private var calendar: List<Date> = listOf()

    private val listener: CalendarListener = {
        calendar = it.ifEmpty {
            listOf()
        }
    }

    fun loadCalendar() {
        val currentDate = Date()
        _selectedData.value = currentDate
        _daysInMonth.value = calendarRepository.getCalendar()
    }

    override fun onCleared() {
        super.onCleared()
        calendarRepository.removeListener(listener)
    }

    override fun invoke(position: Int) {
        _selectedData.value = _daysInMonth.value!![position]
    }
}
