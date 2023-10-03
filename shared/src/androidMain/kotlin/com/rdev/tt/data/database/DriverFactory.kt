package com.rdev.tt.data.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import java.io.FileOutputStream
import java.io.InputStream

actual class DriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        ensureDbReady()
        return AndroidSqliteDriver(AppDb.Schema, context, DB_NAME)
    }

    private fun ensureDbReady() {
        val database = context.getDatabasePath(DB_NAME)

        if (!database.exists()) {
            val inputStream = context.assets.open(DB_NAME)
            val outputStream = FileOutputStream(database.absolutePath)

            inputStream.use { input: InputStream ->
                outputStream.use { output: FileOutputStream ->
                    input.copyTo(output)
                }
            }
        }
    }

    companion object {
        private const val DB_NAME = "app.db"
    }
}