package com.rdev.tt.data.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.*
import co.touchlab.sqliter.DatabaseConfiguration

actual class DriverFactory(private val dbName: String, private val basePath: String) {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = AppDb.Schema,
            name = dbName,
            onConfiguration = { config ->
                config.copy(
                    extendedConfig = DatabaseConfiguration.Extended(basePath = basePath)
                )
            }
        )
    }
}