package au.com.beba.chainReaction.di

import javax.inject.Scope

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class ActivityScope

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class ServiceScope

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class FragmentScope