package ksu.katara.healthymealplanner.screens.main.tabs.home.diettips

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.healthymealplanner.R
import ksu.katara.healthymealplanner.model.dietTips.DietTipsRepository
import ksu.katara.healthymealplanner.model.dietTips.entities.DietTipDetails
import ksu.katara.healthymealplanner.screens.base.BaseViewModel
import ksu.katara.healthymealplanner.screens.base.Event
import ksu.katara.healthymealplanner.tasks.EmptyResult
import ksu.katara.healthymealplanner.tasks.PendingResult
import ksu.katara.healthymealplanner.tasks.StatusResult
import ksu.katara.healthymealplanner.tasks.SuccessResult

class DietTipDetailsViewModel(
    private val dietTipsRepository: DietTipsRepository,
) : BaseViewModel() {

    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    private val currentState: State get() = state.value!!

    init {
        _state.value = State(
            dietTipDetailsResult = EmptyResult(),
        )
    }

    fun loadDietTipDetails(dietTipId: Long) {
        if (currentState.dietTipDetailsResult !is EmptyResult) return

        _state.value = currentState.copy(dietTipDetailsResult = PendingResult())

        dietTipsRepository.getDietTipDetailsById(dietTipId)
            .onSuccess {
                _state.value = currentState.copy(dietTipDetailsResult = SuccessResult(it))
            }
            .autoCancel()
    }

    data class State(
        val dietTipDetailsResult: StatusResult<DietTipDetails>,
    ) {

        val showContent: Boolean get() = dietTipDetailsResult is SuccessResult
        val showProgress: Boolean get() = dietTipDetailsResult is PendingResult
    }
}