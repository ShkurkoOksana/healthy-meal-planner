package ksu.katara.healthymealplanner.mvvm.di

import android.app.Activity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import ksu.katara.healthymealplanner.foundation.navigator.Navigator
import ksu.katara.healthymealplanner.foundation.views.FragmentsHolder

@Module
@InstallIn(ActivityComponent::class)
class ActivityModule {

    @Provides
    fun provideActivityAndNavigator(
        activity: Activity
    ): Navigator {
        return (activity as FragmentsHolder).getActivityScopeViewModel().navigator
    }

}