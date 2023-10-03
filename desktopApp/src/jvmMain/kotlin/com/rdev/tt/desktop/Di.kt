package com.rdev.tt.desktop

import com.rdev.tt.data.database.DriverFactory
import org.koin.dsl.module

val dbDriverModule = module {
    single { DriverFactory() }
}