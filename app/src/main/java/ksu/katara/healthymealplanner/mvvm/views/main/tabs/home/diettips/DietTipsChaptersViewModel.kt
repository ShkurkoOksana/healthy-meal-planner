package ksu.katara.healthymealplanner.mvvm.views.main.tabs.home.diettips

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import ksu.katara.healthymealplanner.R
import ksu.katara.healthymealplanner.foundation.navigator.Navigator
import ksu.katara.healthymealplanner.foundation.uiactions.UiActions
import ksu.katara.healthymealplanner.foundation.views.BaseViewModel
import ksu.katara.healthymealplanner.foundation.views.LiveResult
import ksu.katara.healthymealplanner.foundation.views.MutableLiveResult
import ksu.katara.healthymealplanner.mvvm.model.dietTips.DietTipsRepository
import ksu.katara.healthymealplanner.mvvm.model.dietTips.entities.DietTipChapter
import ksu.katara.healthymealplanner.mvvm.views.main.tabs.home.diettips.DietTipsChaptersFragment.Screen

class DietTipsChaptersViewModel(
    screen: Screen,
    private val navigator: Navigator,
    private val uiActions: UiActions,
    private val dietTipsRepository: DietTipsRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel(), DietTipActionListener {

    private val _dietTipsChapters = MutableLiveResult<List<DietTipChapter>>()
    val dietTipsChapters: LiveResult<List<DietTipChapter>> = _dietTipsChapters

    private val _screenTitle = MutableLiveData<String>()
    val screenTitle: LiveData<String> = _screenTitle

    init {
        _screenTitle.value = uiActions.getString(R.string.diet_tips_chapters_title)
        loadDietTipsChapters()
    }

    private fun loadDietTipsChapters() = into(_dietTipsChapters){
        dietTipsRepository.loadChapters()
    }

    override fun onDietTipPressed(dietTipId: Long) {
        val screen = DietTipDetailsFragment.Screen(dietTipId)
        navigator.launch(R.id.dietTipDetailsFragment, DietTipDetailsFragment.createArgs(screen))
    }

    fun tryAgain() {
        loadDietTipsChapters()
    }
}