package au.com.beba.chainReaction.di.module

import au.com.beba.chainReaction.App
import au.com.beba.chainReaction.feature.LocalPreferences
import au.com.beba.chainReaction.feature.LocalPreferencesImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides @Singleton
    fun localPreferences(application: App): LocalPreferences = LocalPreferencesImpl(application.applicationContext)
}