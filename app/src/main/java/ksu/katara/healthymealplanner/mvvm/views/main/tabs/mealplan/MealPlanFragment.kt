package ksu.katara.healthymealplanner.mvvm.views.main.tabs.mealplan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import dagger.hilt.android.AndroidEntryPoint
import ksu.katara.healthymealplanner.databinding.FragmentMealPlanBinding
import ksu.katara.healthymealplanner.foundation.views.BaseFragment
import ksu.katara.healthymealplanner.foundation.views.BaseScreen
import ksu.katara.healthymealplanner.foundation.views.HasScreenTitle
import ksu.katara.healthymealplanner.mvvm.views.main.tabs.home.MealTypes
import java.util.Date

@AndroidEntryPoint
class MealPlanFragment : BaseFragment(), HasScreenTitle {

    /**
     * This screen has not arguments.
     */
    class Screen : BaseScreen

    private lateinit var binding: FragmentMealPlanBinding

    private lateinit var calendarAdapter: CalendarAdapter

    override val viewModel by viewModels<MealPlanViewModel>()

    override fun getScreenTitle(): String? = viewModel.screenTitle.value

    private var selectedDate = Date()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMealPlanBinding.inflate(layoutInflater, container, false)
        arguments = bundleOf(BaseScreen.ARG_SCREEN to Screen())
        initView()
        return binding.root
    }

    private fun initView() {
        initCalendar()
        initMealPlanForDate(selectedDate)
    }

    private fun initCalendar() {
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.calendarRecyclerView)
        setUpCalendar()
    }

    private fun setUpCalendar() {
        calendarAdapter = CalendarAdapter(viewModel)
        viewModel.daysInMonth.observe(viewLifecycleOwner) {
            calendarAdapter.daysInMonth = it
        }
        viewModel.selectedData.observe(viewLifecycleOwner) {
            selectedDate = it
            initMealPlanForDate(it)
        }
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.calendarRecyclerView.layoutManager = layoutManager
        binding.calendarRecyclerView.adapter = calendarAdapter
    }

    private fun initMealPlanForDate(selectedDate: Date) {
        binding.breakfastForDateDetailsButton.setOnClickListener {
            viewModel.onMealPlanForSelectedDateItemPressed(selectedDate, MealTypes.BREAKFAST)
        }
        binding.lunchForDateDetailsButton.setOnClickListener {
            viewModel.onMealPlanForSelectedDateItemPressed(selectedDate, MealTypes.LUNCH)
        }
        binding.dinnerForDateDetailsButton.setOnClickListener {
            viewModel.onMealPlanForSelectedDateItemPressed(selectedDate, MealTypes.DINNER)
        }
        binding.snackForDateDetailsButton.setOnClickListener {
            viewModel.onMealPlanForSelectedDateItemPressed(selectedDate, MealTypes.SNACK)
        }
    }

    companion object {
        fun createArgs(screen: BaseScreen) = bundleOf(BaseScreen.ARG_SCREEN to screen)
    }
}
