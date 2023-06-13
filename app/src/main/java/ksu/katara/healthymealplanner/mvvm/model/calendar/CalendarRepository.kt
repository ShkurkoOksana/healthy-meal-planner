package ksu.katara.healthymealplanner.mvvm.model.calendar

import ksu.katara.healthymealplanner.foundation.model.Repository
import java.util.Date

typealias CalendarListener = (calendar: List<Date>) -> Unit

/**
 * Repository of calendar interface.
 *
 * Provides access to the days of the month in a certain range.
 */
interface CalendarRepository : Repository {

    /**
     * Get the list of days of the month in a certain range.
     */
    fun getCalendar(): MutableList<Date>

    /**
     * Listen for the current days of the month.
     * The listener is triggered immediately with the current value when calling this method.
     */
    fun addListener(listener: CalendarListener)

    /**
     * Stop listening for the current days of the month.
     */
    fun removeListener(listener: CalendarListener)

}