package ksu.katara.healthymealplanner.mvvm.screens.main.tabs.home.diettips

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.mvvm.model.dietTips.DietTipsListener
import ksu.katara.healthymealplanner.mvvm.model.dietTips.DietTipsRepository
import ksu.katara.healthymealplanner.mvvm.model.dietTips.entities.DietTip
import ksu.katara.healthymealplanner.mvvm.screens.base.BaseViewModel
import ksu.katara.healthymealplanner.mvvm.screens.base.Event
import ksu.katara.healthymealplanner.mvvm.tasks.EmptyResult
import ksu.katara.healthymealplanner.mvvm.tasks.PendingResult
import ksu.katara.healthymealplanner.mvvm.tasks.StatusResult
import ksu.katara.healthymealplanner.mvvm.tasks.SuccessResult

class DietTipsViewModel(
    private val dietTipsRepository: DietTipsRepository
) : BaseViewModel(), DietTipActionListener {

    private val _dietTips = MutableLiveData<StatusResult<List<DietTip>>>()
    val dietTips: LiveData<StatusResult<List<DietTip>>> = _dietTips

    private val _actionShowToast = MutableLiveData<Event<Int>>()
    val actionShowToast: LiveData<Event<Int>> = _actionShowToast

    private val _actionShowDetails = MutableLiveData<Event<DietTip>>()
    val actionShowDetails: LiveData<Event<DietTip>> = _actionShowDetails

    private var dietTipsResult: StatusResult<List<DietTip>> = EmptyResult()
        set(value) {
            field = value
            notifyUpdates()
        }

    private val listener: DietTipsListener = {
        dietTipsResult = if (it.isEmpty()) {
            EmptyResult()
        } else {
            SuccessResult(it)
        }
    }

    init {
        dietTipsRepository.addDietTipsListener(listener)
        loadDietTips()
    }

    private fun loadDietTips() {
        dietTipsResult = PendingResult()
        dietTipsRepository.loadDietTips()
            .onError {
                _actionShowToast.value = Event(R.string.cant_load_diet_tips_chapters)
            }
            .autoCancel()
    }

    override fun onCleared() {
        super.onCleared()
        dietTipsRepository.removeDietTipsListener(listener)
    }

    private fun notifyUpdates() {
        _dietTips.postValue(dietTipsResult)
    }

    override fun invoke(dietTip: DietTip) {
        _actionShowDetails.value = Event(dietTip)
    }
}