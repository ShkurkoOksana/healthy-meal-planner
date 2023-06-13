package ksu.katara.healthymealplanner.mvvm.screens.main.tabs.home.diettips

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.mvvm.model.dietTips.DietTipsChaptersListener
import ksu.katara.healthymealplanner.mvvm.model.dietTips.DietTipsRepository
import ksu.katara.healthymealplanner.mvvm.model.dietTips.entities.DietTipsChapter
import ksu.katara.healthymealplanner.mvvm.screens.base.BaseViewModel
import ksu.katara.healthymealplanner.mvvm.screens.base.Event
import ksu.katara.healthymealplanner.mvvm.tasks.EmptyResult
import ksu.katara.healthymealplanner.mvvm.tasks.PendingResult
import ksu.katara.healthymealplanner.mvvm.tasks.StatusResult
import ksu.katara.healthymealplanner.mvvm.tasks.SuccessResult

class DietTipsChaptersViewModel(
    private val dietTipsRepository: DietTipsRepository
) : BaseViewModel() {

    private val _dietTipsChapters = MutableLiveData<StatusResult<List<DietTipsChapter>>>()
    val dietTipsChapters: LiveData<StatusResult<List<DietTipsChapter>>> = _dietTipsChapters

    private val _actionShowToast = MutableLiveData<Event<Int>>()
    val actionShowToast: LiveData<Event<Int>> = _actionShowToast

    private var dietTipsChapterResult: StatusResult<List<DietTipsChapter>> = EmptyResult()
        set(value) {
            field = value
            notifyUpdates()
        }

    private val listener: DietTipsChaptersListener = {
        dietTipsChapterResult = if (it.isEmpty()) {
            EmptyResult()
        } else {
            SuccessResult(it)
        }
    }

    init {
        dietTipsRepository.addDietTipsChaptersListener(listener)
        loadDietTipsChapters()
    }

    private fun loadDietTipsChapters() {
        dietTipsChapterResult = PendingResult()
        dietTipsRepository.loadDietTipsChapters()
            .onError {
                _actionShowToast.value = Event(R.string.cant_load_diet_tips_chapters)
            }
            .autoCancel()
    }

    override fun onCleared() {
        super.onCleared()
        dietTipsRepository.removeDietTipsChaptersListener(listener)
    }

    private fun notifyUpdates() {
        _dietTipsChapters.postValue(dietTipsChapterResult)
    }
}