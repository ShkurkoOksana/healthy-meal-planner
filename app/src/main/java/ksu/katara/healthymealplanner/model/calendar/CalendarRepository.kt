package ksu.katara.healthymealplanner.model.calendar

import java.util.Date

interface CalendarRepository {

    fun getCalendar(): MutableList<Date>

    fun addListener(listener: CalendarListener)

    fun removeListener(listener: CalendarListener)
}