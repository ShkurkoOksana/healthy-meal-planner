package ksu.katara.healthymealplanner.mvvm.data.calendar

import ksu.katara.healthymealplanner.mvvm.domain.calendar.CalendarListener
import ksu.katara.healthymealplanner.mvvm.domain.calendar.CalendarRepository
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * Simple in-memory implementation of [CalendarRepository]
 */
class InMemoryCalendarRepository @Inject constructor()  : CalendarRepository {

    private var calendar = mutableListOf<Date>()
    private val listeners = mutableSetOf<CalendarListener>()

    override fun getCalendar(): MutableList<Date> {
        val minDay = Calendar.getInstance(Locale.ENGLISH)
        val maxDay = getMaxDay(MAX_COUNT_MONTH)
        calendar = getCalendar(minDay, maxDay)
        notifyChanges()
        return calendar
    }

    private fun getCalendar(minDay: Calendar, maxDay: Calendar): MutableList<Date> {
        val days = mutableListOf<Date>()
        var data = minDay.time
        while (data < maxDay.time) {
            days.add(data)
            minDay.add(Calendar.DAY_OF_MONTH, 1)
            data = minDay.time
        }
        return days
    }

    private fun getMaxDay(month: Int): Calendar {
        val date = Calendar.getInstance(Locale.ENGLISH)
        date.add(Calendar.MONTH, month)
        return date
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