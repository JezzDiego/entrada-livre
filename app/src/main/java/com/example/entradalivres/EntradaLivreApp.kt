package com.example.entradalivres

import android.app.Application
import com.example.entradalivres.di.AppContainer

class EntradaLivreApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this, this)
    }
}

