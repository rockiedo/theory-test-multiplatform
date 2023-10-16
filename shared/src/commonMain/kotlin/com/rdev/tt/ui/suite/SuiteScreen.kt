package com.rdev.tt.ui.suite

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.rdev.tt._utils.safePop
import com.rdev.tt.core_model.Suite
import com.rdev.tt.ui.review.ReviewScreen

data class SuiteScreen(
    private val suite: Suite,
    private val isDoingTest: Boolean = false
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<SuiteViewModel>()
        val uiState by viewModel.uiState.collectAsState()

        LaunchedEffect(viewModel) {
            viewModel.loadSuite(suite.id)
        }

        when (uiState) {
            SuiteState.Loading, SuiteState.Error -> {
                Box(Modifier.fillMaxSize()) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
            }

            is SuiteState.Content -> {
                (uiState as SuiteState.Content).let { content ->
                    SuiteComp(
                        suite.name,
                        content.questions,
                        isDoingTest,
                        viewModel,
                        Modifier.fillMaxSize(),
                        onBackPress = { navigator.safePop() },
                        openResult = { answers ->
                            navigator.replace(
                                ReviewScreen(
                                    suiteName = suite.name,
                                    questions = content.questions,
                                    userAnswers = answers
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}