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
import com.rdev.tt._utils.koinViewModel
import com.rdev.tt.core_model.Suite
import com.rdev.tt.ui.test_suite.TestSuiteCompactComp
import com.rdev.tt.ui.test_suite.TestSuiteState
import com.rdev.tt.ui.test_suite.TestSuiteViewModel

data class SuiteScreen(
    private val suite: Suite,
    private val isDoingTest: Boolean = false
) : Screen {
    @Composable
    override fun Content() {
        val viewModel = koinViewModel(TestSuiteViewModel::class)
        val uiState by viewModel.uiState.collectAsState()

        LaunchedEffect(viewModel) {
            viewModel.loadTestSuite(suite.id)
        }

        when (uiState) {
            TestSuiteState.Loading, TestSuiteState.Error -> {
                Box(Modifier.fillMaxSize()) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
            }

            is TestSuiteState.Content -> {
                (uiState as TestSuiteState.Content).let { content ->
                    TestSuiteCompactComp(
                        suite,
                        content.questions,
                        isDoingTest,
                        viewModel,
                        Modifier.fillMaxSize(),
                        onBackPress = {
                            // TODO: implement
                        },
                        openResult = { answers ->
                            // TODO: implement
                        }
                    )
                }
            }
        }
    }
}