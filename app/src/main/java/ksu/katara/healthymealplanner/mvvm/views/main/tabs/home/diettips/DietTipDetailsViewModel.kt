package ksu.katara.healthymealplanner.mvvm.views.main.tabs.home.diettips

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.foundation.model.StatusResult
import ksu.katara.healthymealplanner.foundation.uiactions.UiActions
import ksu.katara.healthymealplanner.foundation.views.BaseScreen
import ksu.katara.healthymealplanner.foundation.views.BaseViewModel
import ksu.katara.healthymealplanner.mvvm.model.dietTips.DietTipsRepository
import ksu.katara.healthymealplanner.mvvm.model.dietTips.entities.DietTipDetailSteps
import ksu.katara.healthymealplanner.mvvm.model.dietTips.entities.DietTipDetails
import ksu.katara.healthymealplanner.mvvm.views.main.tabs.home.diettips.DietTipDetailsFragment.Screen

data class DietTipDetailsSteps(
    val dietTipDetails: DietTipDetails,
    val steps: List<DietTipDetailSteps>
)

class DietTipDetailsViewModel @AssistedInject constructor(
    @Assisted screen: BaseScreen,
    uiActions: UiActions,
    private val dietTipsRepository: DietTipsRepository,
) : BaseViewModel() {

    private val _dietTipDetailsSteps = MutableLiveData<StatusResult<DietTipDetailsSteps>>()
    val dietTipDetailsSteps: LiveData<StatusResult<DietTipDetailsSteps>> = _dietTipDetailsSteps

    private val _screenTitle = MutableLiveData<String>()
    val screenTitle: LiveData<String> = _screenTitle

    private val dietTipId = (screen as Screen).dietTipId

    init {
        _screenTitle.value = uiActions.getString(R.string.diet_tips_details_title)
        loadDietTipDetailsSteps(dietTipId)
    }

    private fun loadDietTipDetailsSteps(id: Long) = into(_dietTipDetailsSteps) {
        dietTipsRepository.loadDietTipDetailsStepsById(id)
    }

    fun tryAgain() {
        loadDietTipDetailsSteps(dietTipId)
    }

    @AssistedFactory
    interface Factory {
        fun create(screen: BaseScreen): DietTipDetailsViewModel
    }

}