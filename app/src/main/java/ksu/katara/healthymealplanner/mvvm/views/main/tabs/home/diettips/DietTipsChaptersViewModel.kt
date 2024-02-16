package ksu.katara.healthymealplanner.mvvm.views.main.tabs.home.diettips

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.foundation.navigator.Navigator
import ksu.katara.healthymealplanner.foundation.uiactions.UiActions
import ksu.katara.healthymealplanner.foundation.views.BaseViewModel
import ksu.katara.healthymealplanner.foundation.views.LiveResult
import ksu.katara.healthymealplanner.foundation.views.MutableLiveResult
import ksu.katara.healthymealplanner.mvvm.model.dietTips.DietTipsRepository
import ksu.katara.healthymealplanner.mvvm.model.dietTips.entities.DietTip
import ksu.katara.healthymealplanner.mvvm.model.dietTips.entities.DietTipChapter
import javax.inject.Inject

data class ChapterDietTips(
    val chapter: DietTipChapter,
    val dietTips: List<DietTip>
)

@HiltViewModel
class DietTipsChaptersViewModel @Inject constructor(
    private val navigator: Navigator,
    uiActions: UiActions,
    private val dietTipsRepository: DietTipsRepository,
) : BaseViewModel(), DietTipActionListener {

    private val _chapterDietTipsList = MutableLiveResult<List<ChapterDietTips>>()
    val chapterDietTipsList: LiveResult<List<ChapterDietTips>> = _chapterDietTipsList

    private val _screenTitle = MutableLiveData<String>()
    val screenTitle: LiveData<String> = _screenTitle

    init {
        _screenTitle.value = uiActions.getString(R.string.diet_tips_chapters_title)
        loadDietTipsChapters()
    }

    private fun loadDietTipsChapters() = into(_chapterDietTipsList) {
        dietTipsRepository.loadChapterDietTipsList()
    }

    override fun onDietTipPressed(dietTipId: Long) {
        val screen = DietTipDetailsFragment.Screen(dietTipId)
        navigator.launch(R.id.dietTipDetailsFragment, DietTipDetailsFragment.createArgs(screen))
    }

    fun tryAgain() {
        loadDietTipsChapters()
    }

}