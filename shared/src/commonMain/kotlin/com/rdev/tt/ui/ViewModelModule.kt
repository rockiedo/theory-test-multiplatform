package com.rdev.tt.ui

import com.rdev.tt.ui.home.HomeViewModel
import com.rdev.tt.ui.suite.SuiteViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val viewModelModule = module {
    single { NavigationBus() }

    factoryOf(::HomeViewModel)
    factoryOf(::SuiteViewModel)
}