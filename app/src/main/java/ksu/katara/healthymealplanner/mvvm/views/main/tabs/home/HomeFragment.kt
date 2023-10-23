package ksu.katara.healthymealplanner.mvvm.views.main.tabs.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.databinding.FragmentHomeBinding
import ksu.katara.healthymealplanner.foundation.views.BaseFragment
import ksu.katara.healthymealplanner.foundation.views.BaseScreen
import ksu.katara.healthymealplanner.foundation.views.HasScreenTitle
import ksu.katara.healthymealplanner.foundation.views.onTryAgain
import ksu.katara.healthymealplanner.foundation.views.renderSimpleResult
import ksu.katara.healthymealplanner.foundation.views.screenViewModel
import ksu.katara.healthymealplanner.mvvm.views.main.tabs.home.diettips.DietTipsAdapter
import ksu.katara.healthymealplanner.mvvm.views.main.tabs.home.diettips.DietTipsChaptersFragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val sdf = SimpleDateFormat("dd MMMM", Locale.getDefault())

class HomeFragment : BaseFragment(), HasScreenTitle {

    /**
     * This screen has not arguments
     */
    class Screen : BaseScreen

    private lateinit var binding: FragmentHomeBinding

    private lateinit var dietTipsAdapter: DietTipsAdapter

    override val viewModel by screenViewModel<HomeViewModel>()

    override fun getScreenTitle(): String? = viewModel.screenTitle.value

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        arguments = bundleOf(BaseScreen.ARG_SCREEN to Screen())
        initView()
        return binding.root
    }

    private fun initView() {
        initProfile()
        initDietTips()
        initMealPlanForToday(currentDate = Date())
        initDiaryDetails()
    }

    private fun initProfile() {
        binding.profileDetailsButton.setOnClickListener { onProfileDetailsButtonPressed() }
    }

    private fun initDiaryDetails() {
        binding.diaryDetailsButton.setOnClickListener { onDiaryDetailsButtonPressed() }
    }

    private fun onDiaryDetailsButtonPressed() {
        Toast.makeText(requireContext(), R.string.toast_functionality_is_not_available, Toast.LENGTH_SHORT).show()
    }

    private fun onProfileDetailsButtonPressed() {
        Toast.makeText(requireContext(), R.string.toast_functionality_is_not_available, Toast.LENGTH_SHORT).show()
    }

    private fun initMealPlanForToday(currentDate: Date) = with(binding) {
        breakfastForDateDetailsButton.setOnClickListener {
            viewModel.onMealPlanForDateItemPressed(R.id.mealPlanForDateRecipesFragment, currentDate, MealTypes.BREAKFAST)
        }
        lunchForDateDetailsButton.setOnClickListener {
            viewModel.onMealPlanForDateItemPressed(R.id.mealPlanForDateRecipesFragment, currentDate, MealTypes.LUNCH)
        }
        dinnerForDateDetailsButton.setOnClickListener {
            viewModel.onMealPlanForDateItemPressed(R.id.mealPlanForDateRecipesFragment, currentDate, MealTypes.DINNER)
        }
        snackForDateDetailsButton.setOnClickListener {
            viewModel.onMealPlanForDateItemPressed(R.id.mealPlanForDateRecipesFragment, currentDate, MealTypes.SNACK)
        }
    }

    private fun initDietTips() {
        binding.dietTipsMoreTextView.setOnClickListener {
            val screen = DietTipsChaptersFragment.Screen()
            viewModel.onMorePressed(R.id.dietTipsChaptersFragment, DietTipsChaptersFragment.createArgs(screen))
        }
        initDietTipsRecycleView()
        onTryAgain(binding.root) {
            viewModel.tryAgain()
        }
    }

    private fun initDietTipsRecycleView() {
        dietTipsAdapter = DietTipsAdapter(viewModel)
        viewModel.dietTips.observe(viewLifecycleOwner) { result ->
            renderSimpleResult(
                root = binding.dietTipsContainer,
                result = result,
                onSuccess = {
                    dietTipsAdapter.dietTips = it
                }
            )
        }
        val dietTipsLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.dietTipsRecyclerView.layoutManager = dietTipsLayoutManager
        binding.dietTipsRecyclerView.adapter = dietTipsAdapter
    }
}

enum class MealTypes(
    val mealName: String
) {
    BREAKFAST("Завтрак"),
    LUNCH("Обед"),
    DINNER("Ужин"),
    SNACK("Перекус");
}