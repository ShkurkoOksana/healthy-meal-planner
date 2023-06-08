package ksu.katara.healthymealplanner.model.calendar

import java.util.Calendar
import java.util.Date
import java.util.Locale

typealias CalendarListener = (calendar: List<Date>) -> Unit

class InMemoryCalendarRepository : CalendarRepository {

    private var calendar = mutableListOf<Date>()
    private val listeners = mutableSetOf<CalendarListener>()

    override fun getCalendar(): MutableList<Date> {
        val minDayOfCalendar = Calendar.getInstance(Locale.ENGLISH)
        val maxDayOfCalendar = getCalendarMinMaxDate(MAX_COUNT_MONTH)
        calendar = getDaysList(minDayOfCalendar, maxDayOfCalendar)
        notifyChanges()
        return calendar
    }

    private fun getDaysList(minDayOfCalendar: Calendar, maxDayOfCalendar: Calendar): MutableList<Date> {
        val daysList = mutableListOf<Date>()
        var data = minDayOfCalendar.time
        while (data < maxDayOfCalendar.time) {
            daysList.add(data)
            minDayOfCalendar.add(Calendar.DAY_OF_MONTH, 1)
            data = minDayOfCalendar.time
        }
        return daysList
    }

    private fun getCalendarMinMaxDate(month: Int): Calendar {
        val currentDate = Calendar.getInstance(Locale.ENGLISH)
        currentDate.add(Calendar.MONTH, month)
        return currentDate
    }

    override fun addListener(listener: CalendarListener) {
        listeners.add(listener)
        listener.invoke(calendar)
    }

    override fun removeListener(listener: CalendarListener) {
        listeners.remove(listener)
    }

    private fun notifyChanges() {
        listeners.forEach { it.invoke(calendar) }
    }

    companion object {
        const val MAX_COUNT_MONTH = 2
    }
}