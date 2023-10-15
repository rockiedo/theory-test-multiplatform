package com.rdev.tt

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.rdev.tt._utils.DarkColors
import com.rdev.tt._utils.LightColors
import com.rdev.tt.core_model.Category
import com.rdev.tt.core_model.Question
import com.rdev.tt.core_model.Suite
import com.rdev.tt.ui.home.HomeScreen
import com.rdev.tt.ui.rememberNavController
import com.rdev.tt.ui.test_result.TestResultScreen
import com.rdev.tt.ui.suite.TestSuiteScreenLegacy

sealed interface AppNavItem {
    data object SuiteList : AppNavItem
    data class Test(val suite: Suite) : AppNavItem
    data class TestResult(
        val suiteName: String,
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
//            HomeScreen()
        }
    )
}

@Composable
fun TheoryTestVoyagerApp(
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
            Navigator(screen = HomeScreen) { navigator ->
                SlideTransition(navigator)
            }
        }
    )
}

@Composable
private fun HomeScreen() {
    val navController = rememberNavController(AppNavItem::class, AppNavItem.SuiteList)
    val currentNavItem = navController.currentNavItem

    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        when (currentNavItem) {
            AppNavItem.SuiteList -> {
            }

            is AppNavItem.Test -> {
                TestSuiteScreenLegacy(
                    suite = currentNavItem.suite,
                    isDoingTest = currentNavItem.suite.categories.contains(Category.TEST),
                    onBackPress = {
                        navController.navTo(AppNavItem.SuiteList)
                    },
                    openResult = { questions, answers ->
                        navController.navTo(
                            AppNavItem.TestResult(
                                currentNavItem.suite.name,
                                questions,
                                answers
                            )
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                )
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