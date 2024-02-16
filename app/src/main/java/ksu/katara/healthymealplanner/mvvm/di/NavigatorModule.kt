package ksu.katara.healthymealplanner.mvvm.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ksu.katara.healthymealplanner.foundation.navigator.IntermediateNavigator
import ksu.katara.healthymealplanner.foundation.navigator.Navigator

@Module
@InstallIn(SingletonComponent::class)
abstract class NavigatorModule {

    @Binds
    abstract fun bindIntermediateNavigatorAndNavigator(
        intermediateNavigator: IntermediateNavigator
    ): Navigator


}