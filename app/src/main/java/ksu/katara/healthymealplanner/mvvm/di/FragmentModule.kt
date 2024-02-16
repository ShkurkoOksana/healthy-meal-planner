package ksu.katara.healthymealplanner.mvvm.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import ksu.katara.healthymealplanner.foundation.views.BaseFragment
import ksu.katara.healthymealplanner.foundation.views.BaseScreen

@Module
@InstallIn(FragmentComponent::class)
class FragmentModule {

    @Provides
    fun provideFragmentAndScreen(
        fragment: BaseFragment
    ): BaseScreen {
        return fragment.requireArguments().getSerializable(BaseScreen.ARG_SCREEN) as BaseScreen
    }

}