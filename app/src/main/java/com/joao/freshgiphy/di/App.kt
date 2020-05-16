package com.joao.freshgiphy.di

import android.app.Application

class App : Application() {

    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)
    }

}