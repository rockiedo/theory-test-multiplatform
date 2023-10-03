package com.rdev.tt.android

import com.rdev.tt.data.database.DriverFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dbDriverModule = module {
    single {
        DriverFactory(androidContext())
    }
}