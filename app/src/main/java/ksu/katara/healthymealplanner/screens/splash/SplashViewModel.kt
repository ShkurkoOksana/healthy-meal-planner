package ksu.katara.healthymealplanner.screens.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * SplashViewModel checks whether user is signed-in or not.
 */
class SplashViewModel : ViewModel() {
    private val _launchMainScreen = MutableLiveData<Boolean>()
    val launchMainScreen: LiveData<Boolean> = _launchMainScreen

    init {
        viewModelScope.launch {
            delay(200L)
            _launchMainScreen.value = true
        }
    }
}
