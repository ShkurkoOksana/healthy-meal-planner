package ksu.katara.healthymealplanner.mvvm.views.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {
    private val _launchMainScreen = MutableLiveData<Boolean>()
    val launchMainScreen: LiveData<Boolean> = _launchMainScreen

    init {
        viewModelScope.launch {
            delay(2000L)
            _launchMainScreen.value = true
        }
    }
}
