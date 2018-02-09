package au.com.beba.chainReaction.di.module

import au.com.beba.chainReaction.MainActivity
import au.com.beba.chainReaction.di.ActivityScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AndroidBindingModule {

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun mainActivity(): MainActivity

//    @FragmentScope
//    @ContributesAndroidInjector
//    abstract fun mainFragment(): MainFragment
}