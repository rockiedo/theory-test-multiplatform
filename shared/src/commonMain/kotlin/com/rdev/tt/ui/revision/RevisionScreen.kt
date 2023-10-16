package com.rdev.tt.ui.revision

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.rdev.tt._utils.safePop
import com.rdev.tt.core_model.Question
import com.rdev.tt.ui.review.ReviewScreen
import com.rdev.tt.ui.suite.SuiteComp
import com.rdev.tt.ui.suite.SuiteViewModel

private const val SUITE_NAME = "Revise wrong answers"

data class RevisionScreen(
    val questions: List<Question>
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<SuiteViewModel>()

        SuiteComp(
            suiteName = SUITE_NAME,
            questions = questions,
            isDoingTest = true,
            viewModel,
            Modifier.fillMaxSize(),
            onBackPress = { navigator.safePop() },
            openResult = { answers ->
                navigator.replace(
                    ReviewScreen(
                        suiteName = SUITE_NAME,
                        questions = questions,
                        userAnswers = answers
                    )
                )
            }
        )
    }
}