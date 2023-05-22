package ksu.katara.healthymealplanner.screens.main.tabs.mealplan

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.Repositories
import ksu.katara.healthymealplanner.databinding.FragmentMealPlanBinding
import ksu.katara.healthymealplanner.model.meal.enum.MealTypes
import ksu.katara.healthymealplanner.screens.main.tabs.TabsFragmentDirections
import ksu.katara.healthymealplanner.screens.main.tabs.home.sdf
import ksu.katara.healthymealplanner.screens.main.tabs.mealplan.calendar.CalendarAdapter
import ksu.katara.healthymealplanner.screens.main.tabs.mealplan.calendar.CalendarViewModel
import ksu.katara.healthymealplanner.utils.findTopNavController
import ksu.katara.healthymealplanner.utils.viewModelCreator
import java.util.Date

class MealPlanFragment : Fragment(R.layout.fragment_meal_plan) {

    private lateinit var binding: FragmentMealPlanBinding
    private lateinit var calendarAdapter: CalendarAdapter

    private val calendarViewModel by viewModelCreator {
        CalendarViewModel(Repositories.calendarRepository)
    }

    // Перенести инициализацию текущего дня во viewModel
    private var selectedDate = Date()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMealPlanBinding.bind(view)

        initCalendar()

        initMealPlanForDate(selectedDate.toString())
    }

    private fun initCalendar() {
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.calendarRecyclerView)

        setUpCalendar()
    }

    private fun setUpCalendar() {
        calendarViewModel.loadCalendar()
        calendarAdapter = CalendarAdapter(calendarViewModel)

        calendarViewModel.daysInMonth.observe(viewLifecycleOwner) {
            calendarAdapter.daysInMonth = it
        }

        calendarViewModel.selectedData.observe(viewLifecycleOwner) {
            selectedDate = it
            initMealPlanForDate(sdf.format(it))
        }

        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.calendarRecyclerView.layoutManager = layoutManager
        binding.calendarRecyclerView.adapter = calendarAdapter
    }

    private fun initMealPlanForDate(selectedDate: String) {
        binding.breakfastForDateDetailsButton.setOnClickListener {
            onMealPlanForSelectedDateItemPressed(MealTypes.BREAKFAST, selectedDate)
        }

        binding.lunchForDateDetailsButton.setOnClickListener {
            onMealPlanForSelectedDateItemPressed(MealTypes.LUNCH, selectedDate)
        }

        binding.dinnerForDateDetailsButton.setOnClickListener {
            onMealPlanForSelectedDateItemPressed(MealTypes.DINNER, selectedDate)
        }

        binding.snackForDateDetailsButton.setOnClickListener {
            onMealPlanForSelectedDateItemPressed(MealTypes.SNACK, selectedDate)
        }
    }

    private fun onMealPlanForSelectedDateItemPressed(mealTypeName: MealTypes, selectedDate: String) {
        val direction = TabsFragmentDirections.actionTabsFragmentToRecipesFragment(mealTypeName, selectedDate)

        findTopNavController().navigate(direction)
    }
}
