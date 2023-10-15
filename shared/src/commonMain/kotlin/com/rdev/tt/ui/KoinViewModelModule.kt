package com.rdev.tt.ui

import com.rdev.tt._utils.ViewModelFactoryBinding
import com.rdev.tt.ui.home.HomeViewModel
import com.rdev.tt.ui.suite.SuiteViewModel
import org.koin.dsl.module

val viewModelModule = module {
    single {
        ViewModelFactoryBinding(
            SuiteViewModel::class.qualifiedName!! to { SuiteViewModel(get()) },
            HomeViewModel::class.qualifiedName!! to { HomeViewModel(get()) },
        )
    }
}