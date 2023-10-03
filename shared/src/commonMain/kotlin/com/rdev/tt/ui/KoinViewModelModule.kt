package com.rdev.tt.ui

import com.rdev.tt._utils.ViewModelFactoryBinding
import com.rdev.tt.ui.suite_list.SuiteListViewModel
import com.rdev.tt.ui.test_result.TestResultViewModel
import com.rdev.tt.ui.test_suite.TestSuiteViewModel
import org.koin.dsl.module

val viewModelModule = module {
    single {
        ViewModelFactoryBinding(
            TestSuiteViewModel::class.qualifiedName!! to { TestSuiteViewModel(get()) },
            SuiteListViewModel::class.qualifiedName!! to { SuiteListViewModel(get()) },
            TestResultViewModel::class.qualifiedName!! to { TestResultViewModel(get()) }
        )
    }
}