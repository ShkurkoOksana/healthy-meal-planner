package ksu.katara.healthymealplanner.mvvm.views.main.tabs.home.diettips

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.foundation.model.StatusResult
import ksu.katara.healthymealplanner.foundation.navigator.Navigator
import ksu.katara.healthymealplanner.foundation.uiactions.UiActions
import ksu.katara.healthymealplanner.foundation.views.BaseViewModel
import ksu.katara.healthymealplanner.mvvm.model.dietTips.DietTipsRepository
import ksu.katara.healthymealplanner.mvvm.model.dietTips.entities.DietTipDetails
import ksu.katara.healthymealplanner.mvvm.views.main.tabs.home.diettips.DietTipDetailsFragment.Screen

class DietTipDetailsViewModel(
    screen: Screen,
    private val navigator: Navigator,
    private val uiActions: UiActions,
    private val dietTipsRepository: DietTipsRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel() {

    private val _dietTipDetails = MutableLiveData<StatusResult<DietTipDetails>>()
    val dietTipDetails: LiveData<StatusResult<DietTipDetails>> = _dietTipDetails

    private val _screenTitle = MutableLiveData<String>()
    val screenTitle: LiveData<String> = _screenTitle

    private val dietTipId = screen.dietTipId

    init {
        _screenTitle.value = uiActions.getString(R.string.diet_tips_details_title)
        loadDietTipDetails(dietTipId)
    }

    private fun loadDietTipDetails(dietTipId: Long) = into(_dietTipDetails) {
        dietTipsRepository.loadDietTipDetails(dietTipId)
    }

    fun tryAgain() {
        loadDietTipDetails(dietTipId)
    }
}