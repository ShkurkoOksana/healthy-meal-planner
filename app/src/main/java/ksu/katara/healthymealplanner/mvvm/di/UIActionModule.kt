package ksu.katara.healthymealplanner.mvvm.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ksu.katara.healthymealplanner.foundation.uiactions.AndroidUiActions
import ksu.katara.healthymealplanner.foundation.uiactions.UiActions

@Module
@InstallIn(SingletonComponent::class)
abstract class UIActionModule {

    @Binds
    abstract fun bindUIAction(
        androidUiActions: AndroidUiActions
    ): UiActions

}