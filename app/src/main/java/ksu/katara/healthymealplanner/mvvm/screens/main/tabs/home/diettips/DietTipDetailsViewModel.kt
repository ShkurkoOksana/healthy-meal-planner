package ksu.katara.healthymealplanner.mvvm.screens.main.tabs.home.diettips

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.mvvm.model.dietTips.DietTipsRepository
import ksu.katara.healthymealplanner.mvvm.model.dietTips.entities.DietTipDetails
import ksu.katara.healthymealplanner.mvvm.screens.base.BaseViewModel
import ksu.katara.healthymealplanner.mvvm.screens.base.Event
import ksu.katara.healthymealplanner.mvvm.tasks.EmptyResult
import ksu.katara.healthymealplanner.mvvm.tasks.PendingResult
import ksu.katara.healthymealplanner.mvvm.tasks.StatusResult
import ksu.katara.healthymealplanner.mvvm.tasks.SuccessResult

class DietTipDetailsViewModel(
    private val dietTipId: Long,
    private val dietTipsRepository: DietTipsRepository,
) : BaseViewModel() {
    private val _dietTipDetails = MutableLiveData<StatusResult<DietTipDetails>>()
    val dietTipDetails: LiveData<StatusResult<DietTipDetails>> = _dietTipDetails

    private val _actionShowToast = MutableLiveData<Event<Int>>()
    val actionShowToast: LiveData<Event<Int>> = _actionShowToast

    private var dietTipDetailsResult: StatusResult<DietTipDetails> = EmptyResult()
        set(value) {
            field = value
            notifyUpdates()
        }

    init {
        loadDietTipDetails()
    }

    private fun loadDietTipDetails() {
        dietTipDetailsResult = PendingResult()
        dietTipsRepository.loadDietTipDetails(dietTipId)
            .onSuccess {
                dietTipDetailsResult = SuccessResult(it)
            }
            .onError {
                _actionShowToast.value = Event(R.string.cant_load_diet_tip_details)
            }
            .autoCancel()
    }

    private fun notifyUpdates() {
        _dietTipDetails.postValue(dietTipDetailsResult)
    }
}
