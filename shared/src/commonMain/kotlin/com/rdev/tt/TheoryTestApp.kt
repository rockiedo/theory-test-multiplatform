package com.rdev.tt

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rdev.tt._utils.DarkColors
import com.rdev.tt._utils.LightColors
import com.rdev.tt.core_model.Category
import com.rdev.tt.core_model.Question
import com.rdev.tt.core_model.Suite
import com.rdev.tt.ui.rememberNavController
import com.rdev.tt.ui.suite_list.SuiteListScreen
import com.rdev.tt.ui.test_result.TestResultScreen
import com.rdev.tt.ui.test_suite.TestSuiteCompactScreen
import com.rdev.tt.ui.test_suite.TestSuiteScreen

sealed interface AppNavItem {
    data object SuiteList : AppNavItem
    data class Test(val suite: Suite, val category: @Category String) : AppNavItem
    data class TestResult(
        val suiteName: String,
        val category: @Category String,
        val questions: List<Question>,
        val userAnswers: Map<Long, Int>
    ) : AppNavItem
}

@Composable
fun TheoryTestApp(
    darkTheme: Boolean = isSystemInDarkTheme(),
    setSystemBarsColor: @Composable (ColorScheme) -> Unit = {}
) {
    val colors = if (darkTheme) {
        DarkColors
    } else {
        LightColors
    }

    setSystemBarsColor(colors)

    MaterialTheme(
        colorScheme = colors,
        content = {
            HomeScreen()
        }
    )
}


@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
private fun HomeScreen() {
    val navController = rememberNavController(AppNavItem::class, AppNavItem.SuiteList)
    val currentNavItem = navController.currentNavItem

    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        when (currentNavItem) {
            AppNavItem.SuiteList -> {
                SuiteListScreen(
                    modifier = Modifier.fillMaxSize(),
                    onSelectSuite = {
                        navController.navTo(AppNavItem.Test(it, Category.BTT))
                    }
                )
            }

            is AppNavItem.Test -> {
                val windowSizeClass = calculateWindowSizeClass()

                when (windowSizeClass.widthSizeClass) {
                    WindowWidthSizeClass.Compact -> {
                        TestSuiteCompactScreen(
                            suite = currentNavItem.suite,
                            category = currentNavItem.category,
                            onBackPress = {
                                navController.navTo(AppNavItem.SuiteList)
                            },
                            openResult = { questions, answers ->
                                navController.navTo(
                                    AppNavItem.TestResult(
                                        currentNavItem.suite.name,
                                        currentNavItem.category,
                                        questions,
                                        answers
                                    )
                                )
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    WindowWidthSizeClass.Medium, WindowWidthSizeClass.Expanded -> {
                        TestSuiteScreen(
                            suite = currentNavItem.suite,
                            category = currentNavItem.category,
                            onBackPress = {
                                navController.navTo(AppNavItem.SuiteList)
                            },
                            openResult = { questions, answers ->
                                navController.navTo(
                                    AppNavItem.TestResult(
                                        currentNavItem.suite.name,
                                        currentNavItem.category,
                                        questions,
                                        answers
                                    )
                                )
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            is AppNavItem.TestResult -> {
                TestResultScreen(
                    currentNavItem,
                    onClose = {
                        navController.navTo(AppNavItem.SuiteList)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}