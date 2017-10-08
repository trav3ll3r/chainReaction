package au.com.beba.phaserizer.di.component

import au.com.beba.phaserizer.App
import au.com.beba.phaserizer.di.module.AndroidBindingModule
import au.com.beba.phaserizer.di.module.AppModule
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class, AndroidSupportInjectionModule::class , AndroidBindingModule::class))
interface AppComponent : AndroidInjector<App> {
    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<App>()
}