package com.joao.freshgiphy.di

import com.joao.freshgiphy.ui.activities.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(mainActivity: MainActivity)
//    fun inject(moviesViewModel: MoviesViewModel?) TODO
}