package com.rdev.tt.data.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

actual class DriverFactory(private val context: Context) {
    actual fun createDriver(): app.cash.sqldelight.db.SqlDriver {
        return AndroidSqliteDriver(AppDb.Schema, context, "app.db")
    }
}