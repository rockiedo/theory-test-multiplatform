package com.rdev.tt.data.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.io.File

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:$DATASET_DB")

        if (!File(DATASET_DB).exists()) {
            AppDb.Schema.create(driver)
        }

        return driver
    }

    companion object {
        private const val DATASET_DB = ".assets/app.db"
    }
}