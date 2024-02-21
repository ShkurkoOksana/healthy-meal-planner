package ksu.katara.healthymealplanner.mvvm.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ksu.katara.healthymealplanner.foundation.ActivityScopeViewModel
import ksu.katara.healthymealplanner.foundation.views.BaseViewModel
import ksu.katara.healthymealplanner.mvvm.presentation.main.tabs.home.HomeViewModel

@Module
@InstallIn(SingletonComponent::class)
abstract class ViewModelModule {

    @Binds
    abstract fun bindHomeViewModelAndBaseViewModel(
        homeViewModel: HomeViewModel
    ): BaseViewModel

    @Binds
    abstract fun bindActivityScopeViewModelAndViewModel(
        activityScopeViewModel: ActivityScopeViewModel
    ): ViewModel

}