package com.rdev.tt.data

import com.rdev.tt._utils.DateTimeUtils
import com.rdev.tt.data.database.AppDb
import com.rdev.tt.data.database.ColumnMappers
import com.rdev.tt.data.database.DriverFactory
import com.rdev.tt.data.database.QuestionEntity
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dataModule = module {
    single {
        Json { ignoreUnknownKeys = true }
    }

    singleOf(::ColumnMappers)
    singleOf(::DateTimeUtils)

    single {
        val driverFactory = get<DriverFactory>()
        val driver = driverFactory.createDriver()

        val columnMappers = get<ColumnMappers>()
        val adapter = QuestionEntity.Adapter(
            choicesAdapter = columnMappers.listToStringMapper,
            categoriesAdapter = columnMappers.listToStringMapper
        )

        AppDb(driver, adapter)
    }

    singleOf(::AppRepository)
}