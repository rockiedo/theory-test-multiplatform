package com.rdev.tt.ui.test_suite

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.rdev.tt._utils.Spacing
import com.rdev.tt._utils.koinViewModel
import com.rdev.tt.core_model.Category
import com.rdev.tt.core_model.Question
import com.rdev.tt.core_model.Suite
import com.rdev.tt.ui.question.renderQuestion
import kotlinx.coroutines.launch

@Composable
fun TestSuiteCompactScreen(
    suite: Suite,
    category: @Category String,
    onBackPress: () -> Unit,
    openResult: (List<Question>, Map<Long, Int>) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TestSuiteViewModel = koinViewModel(TestSuiteViewModel::class)
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
                    viewModel,
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

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class,
)
@Composable
private fun TestSuiteCompactComp(
    suite: Suite,
    questions: List<Question>,
    category: @Category String,
    viewModel: TestSuiteViewModel,
    modifier: Modifier = Modifier,
    onBackPress: () -> Unit = {},
    openResult: (Map<Long, Int>) -> Unit = {}
) {
    val userAnswers = remember { mutableStateMapOf<Long, Int>() }
    val pagerState = rememberPagerState(pageCount = { questions.size })
    val coroutineScope = rememberCoroutineScope()
    var isDropdownMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier,
        topBar = {
            Surface(shadowElevation = 8.dp) {
                TopAppBar(
                    title = {
                        Text(suite.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackPress) {
                            Icon(Icons.Filled.ChevronLeft, null)
                        }
                    },
                    actions = {
                        Text(
                            "${pagerState.currentPage + 1} / ${questions.size}",
                            modifier = Modifier
                                .padding(end = Spacing.x4)
                                .clickable { isDropdownMenuExpanded = true },
                            textDecoration = TextDecoration.Underline
                        )

                        DropdownMenu(
                            expanded = isDropdownMenuExpanded,
                            onDismissRequest = { isDropdownMenuExpanded = false }
                        ) {
                            questions.forEachIndexed { index, question ->
                                DropdownMenuItem(
                                    text = {
                                        Text("${index + 1}")
                                    },
                                    onClick = {
                                        isDropdownMenuExpanded = false
                                        coroutineScope.launch { pagerState.scrollToPage(index) }
                                    },
                                    trailingIcon = {
                                        if (question.id in userAnswers.keys) {
                                            Icon(
                                                Icons.Filled.RadioButtonChecked,
                                                null,
                                                Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    },
                )
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = userAnswers.size == questions.size,
                enter = fadeIn() + expandIn { IntSize(width = 1, height = 1) }
            ) {
                Button(
                    onClick = { openResult(userAnswers) },
                ) {
                    Text("Review")
                }
            }
        }
    ) { innerPadding ->
        HorizontalPager(
            pagerState,
            modifier = Modifier.fillMaxHeight().padding(innerPadding)
        ) { questionIdx ->
            val question = questions[questionIdx]

            LazyColumn(verticalArrangement = Arrangement.Top, modifier = Modifier.fillMaxSize()) {
                renderQuestion(
                    questionIndex = questionIdx,
                    question = question,
                    category = category,
                    selection = userAnswers[question.id] ?: DEFAULT_ANSWER,
                    isCompactScreen = true,
                    onAnswer = { questionId, answerIdx ->
                        val correctAnswerIdx =
                            questions.firstOrNull { it.id == questionId }?.answerIdx
                        viewModel.recordAnswer(
                            questionId,
                            answerIdx == correctAnswerIdx
                        )

                        userAnswers[questionId] = answerIdx
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.x4),
                    toNextQuestion = null
                )
            }
        }
    }
}