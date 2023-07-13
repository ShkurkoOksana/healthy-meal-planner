package ksu.katara.healthymealplanner.mvvm.views.main.tabs

import androidx.lifecycle.SavedStateHandle
import ksu.katara.healthymealplanner.foundation.navigator.Navigator
import ksu.katara.healthymealplanner.foundation.uiactions.UiActions
import ksu.katara.healthymealplanner.foundation.views.BaseViewModel
import ksu.katara.healthymealplanner.mvvm.views.main.tabs.TabsFragment.Screen

class TabsViewModel(
    screen: Screen,
    private val navigator: Navigator,
    private val uiActions: UiActions,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel()