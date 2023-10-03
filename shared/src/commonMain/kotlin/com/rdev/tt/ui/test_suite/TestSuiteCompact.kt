package com.rdev.tt.ui.test_suite

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize
import com.rdev.tt._utils.Spacing
import com.rdev.tt._utils.koinViewModel
import com.rdev.tt.core_model.Category
import com.rdev.tt.core_model.Question
import com.rdev.tt.core_model.Suite
import com.rdev.tt.ui.question.QuestionComp
import kotlinx.coroutines.launch

@Composable
fun TestSuiteCompactScreen(
    suite: Suite,
    category: @Category String,
    onBackPress: () -> Unit,
    openResult: (List<Question>, Map<Long, Int>) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TestSuiteViewModel = koinViewModel()
) {
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
                    category,
                    modifier,
                    onBackPress,
                    openResult = { answers ->
                        openResult(content.questions, answers)
                    }
                )
            }
        }
    }
}

private const val DEFAULT_ANSWER = -1

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun TestSuiteCompactComp(
    suite: Suite,
    questions: List<Question>,
    category: @Category String,
    modifier: Modifier = Modifier,
    onBackPress: () -> Unit = {},
    openResult: (Map<Long, Int>) -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    val userAnswers = remember { mutableStateMapOf<Long, Int>() }
    val pagerState = rememberPagerState(pageCount = { questions.size })

    Scaffold(
        modifier,
        topBar = {
            TopAppBar(
                title = { Text(suite.name) },
                navigationIcon = {
                    IconButton(onClick = onBackPress) {
                        Icon(Icons.Filled.ChevronLeft, null)
                    }
                },
                actions = {
                    Text(
                        "${pagerState.currentPage + 1} / ${questions.size}",
                        Modifier.padding(end = Spacing.x4)
                    )
                }
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = userAnswers.size == questions.size,
                enter = fadeIn() + expandIn { IntSize(width = 1, height = 1) }
            ) {
                FloatingActionButton(
                    onClick = { openResult(userAnswers) },
                ) {
                    Text("Review")
                }
            }
        }
    ) { innerPadding ->
        HorizontalPager(pagerState) { questionIdx ->
            val question = questions[questionIdx]

            QuestionComp(
                questionIdx,
                question,
                category,
                onAnswer = { questionId, answerIdx ->
                    userAnswers[questionId] = answerIdx
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = Spacing.x4),
                preselect = userAnswers[question.id] ?: DEFAULT_ANSWER,
                toNextQuestion = {
                    if (questionIdx >= questions.lastIndex) return@QuestionComp
                    coroutineScope.launch {
                        pagerState.scrollToPage(pagerState.currentPage + 1)
                    }
                }
            )
        }
    }
}