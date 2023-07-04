package ksu.katara.healthymealplanner.mvvm.views.main.tabs.mealplan

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.databinding.ItemDayOfMonthBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

typealias OnCalendarItemClickListener = (Int) -> Unit

class CalendarAdapter(
    private val mealPlanViewModel: MealPlanViewModel,
) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {
    var daysInMonth: List<Date> = emptyList()
        set(newValue) {
            field = newValue
            notifyDataSetChanged()
        }

    private val yearSDF = SimpleDateFormat("yyyy", Locale.ENGLISH)
    private val monthSDF = SimpleDateFormat("MMM", Locale.ENGLISH)
    private val daySDF = SimpleDateFormat("dd", Locale.ENGLISH)
    private val dayOfWeekSDF = SimpleDateFormat("EEE", Locale.ENGLISH)

    private var index = -1
    private var selectCurrentDate = true
    private val currentDate = Date()
    private val currentYear = yearSDF.format(currentDate)
    private val currentMonth = monthSDF.format(currentDate)
    private val currentDay = daySDF.format(currentDate)

    private val selectedYear = currentYear
    private val selectedMonth = currentMonth
    private val selectedDay = currentDay


    override fun onCreateViewHolder(parent: ViewGroup, position: Int): CalendarViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemDayOfMonthBinding.inflate(inflater, parent, false)
        return CalendarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val date = daysInMonth[position]
        val displayYear = yearSDF.format(date)
        val displayMonth = monthSDF.format(date)
        val displayDay = daySDF.format(date)
        val displayDayOfWeek = dayOfWeekSDF.format(date)
        holder.binding.dayOfWeekTextView.text = displayDayOfWeek
        holder.binding.monthTextView.text = displayMonth
        holder.binding.dayTextView.text = displayDay
        setCalendarListener(holder, position)
        changeVisibilityOfCalendarSelectedItem(holder, displayDay, displayMonth, displayYear, position)
    }

    private fun setCalendarListener(
        holder: CalendarViewHolder,
        position: Int,
    ) {
        holder.binding.dayOfWeekLinearLayout.setOnClickListener {
            index = position
            selectCurrentDate = false
            mealPlanViewModel.invoke(position)
            notifyDataSetChanged()
        }
    }

    private fun changeVisibilityOfCalendarSelectedItem(
        holder: CalendarViewHolder,
        displayDay: String,
        displayMonth: String,
        displayYear: String,
        position: Int,
    ) {
        if (index == position)
            makeCalendarItemSelected(holder)
        else {
            if (displayDay == selectedDay
                && displayMonth == selectedMonth
                && displayYear == selectedYear
                && selectCurrentDate
            )
                makeCalendarItemSelected(holder)
            else
                makeCalendarItemDefault(holder)
        }
    }

    override fun getItemCount(): Int = daysInMonth.size

    class CalendarViewHolder(
        val binding: ItemDayOfMonthBinding,
    ) : RecyclerView.ViewHolder(binding.root)

    private fun makeCalendarItemSelected(holder: CalendarViewHolder) {
        holder.binding.dayOfWeekLinearLayout.setBackgroundResource(R.drawable.ic_calendar_border_shape)
    }

    private fun makeCalendarItemDefault(holder: CalendarViewHolder) {
        holder.binding.dayOfWeekLinearLayout.setBackgroundColor(Color.WHITE)
    }
}