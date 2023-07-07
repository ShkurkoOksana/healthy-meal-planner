package ksu.katara.healthymealplanner.mvvm.views.main.tabs.home.diettips

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.foundation.model.EmptyResult
import ksu.katara.healthymealplanner.foundation.model.ErrorResult
import ksu.katara.healthymealplanner.foundation.model.PendingResult
import ksu.katara.healthymealplanner.foundation.model.StatusResult
import ksu.katara.healthymealplanner.foundation.model.SuccessResult
import ksu.katara.healthymealplanner.foundation.navigator.Navigator
import ksu.katara.healthymealplanner.foundation.uiactions.UiActions
import ksu.katara.healthymealplanner.foundation.views.BaseViewModel
import ksu.katara.healthymealplanner.foundation.views.LiveResult
import ksu.katara.healthymealplanner.foundation.views.MutableLiveResult
import ksu.katara.healthymealplanner.mvvm.model.dietTips.DietTipsChaptersListener
import ksu.katara.healthymealplanner.mvvm.model.dietTips.DietTipsRepository
import ksu.katara.healthymealplanner.mvvm.model.dietTips.entities.DietTipsChapter
import ksu.katara.healthymealplanner.mvvm.views.main.tabs.home.diettips.DietTipsChaptersFragment.Screen

class DietTipsChaptersViewModel(
    screen: Screen,
    private val navigator: Navigator,
    private val uiActions: UiActions,
    private val dietTipsRepository: DietTipsRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(), DietTipActionListener {

    private val _dietTipsChapters = MutableLiveResult<List<DietTipsChapter>>()
    val dietTipsChapters: LiveResult<List<DietTipsChapter>> = _dietTipsChapters

    private val _screenTitle = MutableLiveData<String>()
    val screenTitle: LiveData<String> = _screenTitle

    private var dietTipsChaptersResult: StatusResult<List<DietTipsChapter>> = EmptyResult()
        set(value) {
            field = value
            notifyDietTipsChaptersUpdates()
        }

    private val dietTipsChaptersListener: DietTipsChaptersListener = {
        dietTipsChaptersResult = if (it.isEmpty()) {
            EmptyResult()
        } else {
            SuccessResult(it)
        }
    }

    init {
        _screenTitle.value = uiActions.getString(R.string.diet_tips_chapters_title)
        dietTipsRepository.addDietTipsChaptersListener(dietTipsChaptersListener)
        loadDietTipsChapters()
    }

    private fun loadDietTipsChapters() {
        dietTipsChaptersResult = PendingResult()
        dietTipsRepository.loadDietTipsChapters()
            .onError {
                dietTipsChaptersResult = ErrorResult(it)
            }
            .autoCancel()
    }

    override fun onCleared() {
        super.onCleared()
        dietTipsRepository.removeDietTipsChaptersListener(dietTipsChaptersListener)
    }

    private fun notifyDietTipsChaptersUpdates() {
        _dietTipsChapters.postValue(dietTipsChaptersResult)
    }

    override fun onDietTipPressed(dietTipId: Long) {
        val screen = DietTipDetailsFragment.Screen(dietTipId)
        navigator.launch(R.id.dietTipDetailsFragment, DietTipDetailsFragment.createArgs(screen))
    }
}