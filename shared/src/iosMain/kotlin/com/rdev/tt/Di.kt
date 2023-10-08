package com.rdev.tt

import com.rdev.tt.data.dataModule
import com.rdev.tt.data.database.DriverFactory
import com.rdev.tt.ui.viewModelModule
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun initKoin(dbName: String, basePath: String) {
    val dbDriverModule = module {
        single { DriverFactory(dbName, basePath) }
    }
    startKoin {
        modules(dbDriverModule, dataModule, viewModelModule)
    }
}