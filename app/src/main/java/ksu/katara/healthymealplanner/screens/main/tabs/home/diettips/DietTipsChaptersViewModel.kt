package ksu.katara.healthymealplanner.screens.main.tabs.home.diettips

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.model.dietTips.DietTipsChaptersListener
import ksu.katara.healthymealplanner.model.dietTips.DietTipsRepository
import ksu.katara.healthymealplanner.model.dietTips.entities.DietTipsChapter
import ksu.katara.healthymealplanner.screens.base.BaseViewModel
import ksu.katara.healthymealplanner.screens.base.Event
import ksu.katara.healthymealplanner.tasks.EmptyResult
import ksu.katara.healthymealplanner.tasks.PendingResult
import ksu.katara.healthymealplanner.tasks.StatusResult
import ksu.katara.healthymealplanner.tasks.SuccessResult

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