package com.rdev.tt

import com.rdev.tt.data.dataModule
import com.rdev.tt.data.database.DriverFactory
import com.rdev.tt.ui.viewModelModule
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun initKoin(
    dbName: String,
    dbDir: String,
    imageDir: String
) {
    val dbDriverModule = module {
        single { DriverFactory(dbName, dbDir) }
    }
    val assetModule = module {
        single { ImagePathProvider(imageDir) }
    }
    startKoin {
        modules(dbDriverModule, assetModule, dataModule, viewModelModule)
    }
}