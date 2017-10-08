package au.com.beba.phaserizer.di.module

import au.com.beba.phaserizer.App
import au.com.beba.phaserizer.feature.LocalPreferences
import au.com.beba.phaserizer.feature.LocalPreferencesImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides @Singleton
    fun localPreferences(application: App): LocalPreferences = LocalPreferencesImpl(application.applicationContext)
}