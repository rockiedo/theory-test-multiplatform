package com.rdev.tt.data.database

import app.cash.sqldelight.ColumnAdapter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ColumnMappers(private val json: Json) {
    val listToStringMapper = object : ColumnAdapter<List<String>, String> {
        override fun decode(databaseValue: String): List<String> {
            return json.decodeFromString<List<String>>(databaseValue)
        }

        override fun encode(value: List<String>): String {
            return json.encodeToString(value)
        }
    }
}