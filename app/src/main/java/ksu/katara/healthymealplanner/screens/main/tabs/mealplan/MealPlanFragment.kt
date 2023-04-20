package ksu.katara.healthymealplanner.screens.main.tabs.mealplan

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.databinding.FragmentMealPlanBinding

class MealPlanFragment : Fragment(R.layout.fragment_meal_plan) {

    private lateinit var binding: FragmentMealPlanBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMealPlanBinding.bind(view)
    }
}
