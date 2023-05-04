package ksu.katara.healthymealplanner.screens.main.tabs.home.diettips

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ksu.katara.healthymealplanner.model.dietTips.DietTipsChaptersListener
import ksu.katara.healthymealplanner.model.dietTips.DietTipsRepository
import ksu.katara.healthymealplanner.model.dietTips.entities.DietTipsChapter
import ksu.katara.healthymealplanner.screens.base.BaseViewModel
import ksu.katara.healthymealplanner.tasks.EmptyResult
import ksu.katara.healthymealplanner.tasks.ErrorResult
import ksu.katara.healthymealplanner.tasks.PendingResult
import ksu.katara.healthymealplanner.tasks.StatusResult
import ksu.katara.healthymealplanner.tasks.SuccessResult

data class DietTipsChaptersListItem(
    val dietTipsChapter: DietTipsChapter,
    val isInProgress: Boolean
)

class DietTipsChaptersViewModel(
    private val dietTipsRepository: DietTipsRepository
) : BaseViewModel() {

    private val _dietTipsChapters = MutableLiveData<StatusResult<List<DietTipsChaptersListItem>>>()
    val dietTipsChapters: LiveData<StatusResult<List<DietTipsChaptersListItem>>> = _dietTipsChapters

    private val dietTipsChapterIdsInProgress = mutableSetOf<Long>()
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
                dietTipsChapterResult = ErrorResult(it)
            }
            .autoCancel()
    }

    override fun onCleared() {
        super.onCleared()
        dietTipsRepository.removeDietTipsChaptersListener(listener)
    }

    private fun isInProgress(dietTipsChapter: DietTipsChapter): Boolean {
        return dietTipsChapterIdsInProgress.contains(dietTipsChapter.id)
    }

    private fun notifyUpdates() {
        _dietTipsChapters.postValue(dietTipsChapterResult.resultMap { dietTipsChapters ->
            dietTipsChapters.map { dietTipsChapter -> DietTipsChaptersListItem(dietTipsChapter, isInProgress(dietTipsChapter)) } })
    }
}