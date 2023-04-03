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
    private val dietTipsRepository: DietTipsRepository
) : BaseViewModel() {

    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    private val _actionShowToast = MutableLiveData<Event<Int>>()
    val actionShowToast: LiveData<Event<Int>> = _actionShowToast

    private val _actionGoBack = MutableLiveData<Event<Unit>>()
    val actionGoBack: LiveData<Event<Unit>> = _actionGoBack

    private val currentState: State get() = state.value!!

    init {
        _state.value = State(
            dietTipDetailsResult = EmptyResult(),
            deletingInProgress = false
        )
    }

    fun loadDietTip(userId: Long) {
        if (currentState.dietTipDetailsResult !is EmptyResult) return

        _state.value = currentState.copy(dietTipDetailsResult = PendingResult())

        dietTipsRepository.getById(userId)
            .onSuccess {
                _state.value = currentState.copy(dietTipDetailsResult = SuccessResult(it))
            }
            .onError {
                _actionShowToast.value = Event(R.string.cant_load_diet_tip_details)
                _actionGoBack.value = Event(Unit)
            }
            .autoCancel()
    }

    data class State(
        val dietTipDetailsResult: StatusResult<DietTipDetails>,
        private val deletingInProgress: Boolean
    ) {

        val showContent: Boolean get() = dietTipDetailsResult is SuccessResult
        val showProgress: Boolean get() = dietTipDetailsResult is PendingResult || deletingInProgress
    }
}