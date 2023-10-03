package com.rdev.tt.android

import android.app.Application
import com.rdev.tt.data.dataModule
import com.rdev.tt.ui.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class TheoryTestApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@TheoryTestApplication)
            modules(dbDriverModule, dataModule, viewModelModule)
        }
    }
}