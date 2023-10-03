package com.rdev.tt.ui.test_suite

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rdev.tt._utils.Spacing
import com.rdev.tt._utils.koinViewModel
import com.rdev.tt.core_model.Category
import com.rdev.tt.core_model.Question
import com.rdev.tt.core_model.Suite
import com.rdev.tt.ui.question.QuestionComp
import com.rdev.tt._utils.Spacing as S

@Composable
fun TestSuiteScreen(
    suite: Suite,
    category: @Category String,
    onBackPress: () -> Unit,
    openResult: (List<Question>, Map<Long, Int>) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TestSuiteViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var currentQuestion by remember { mutableStateOf(0) }
    val userAnswers = remember { mutableStateMapOf<Long, Int>() }

    LaunchedEffect(viewModel) {
        viewModel.loadTestSuite(suite.id)
    }

    Column(modifier) {
        when (uiState) {
            TestSuiteState.Loading, TestSuiteState.Error -> {
                Box(Modifier.fillMaxSize()) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
            }

            is TestSuiteState.Content -> {
                (uiState as TestSuiteState.Content).let { content ->
                    TestSuiteHeaderComp(
                        suiteName = suite.name,
                        questionIdx = currentQuestion,
                        questionCount = content.questions.size,
                        onBackPress = onBackPress,
                        toPrevQuestion = { currentQuestion-- },
                        toNextQuestion = { currentQuestion++ },
                        openResult = {
                            openResult(content.questions, userAnswers)
                        },
                        shouldShowReviewButton = {
                            userAnswers.size == content.questions.size
                        },
                        modifier = Modifier.fillMaxWidth().padding(Spacing.x4)
                    )
                }

                Spacer(Modifier.height(S.x4))
                val questionList = (uiState as TestSuiteState.Content).questions

                TestSuiteComp(
                    questions = questionList,
                    userAnswers = userAnswers,
                    questionIdx = currentQuestion,
                    getImageFilePath = { imageName ->
                        viewModel.getImageFilePath(imageName, category)
                    },
                    onAnswer = { questionId, answerIdx ->
                        val correctAnswerIdx = questionList.firstOrNull { it.id == questionId }?.answerIdx
                        viewModel.recordAnswer(
                            questionId,
                            answerIdx == correctAnswerIdx
                        )

                        userAnswers[questionId] = answerIdx
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = S.x4),
                    toNextQuestion = { currentQuestion++ }
                )
            }
        }
    }
}

@Composable
private fun TestSuiteHeaderComp(
    suiteName: String,
    questionIdx: Int,
    questionCount: Int,
    onBackPress: () -> Unit,
    toPrevQuestion: () -> Unit,
    toNextQuestion: () -> Unit,
    openResult: () -> Unit,
    shouldShowReviewButton: () -> Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier,
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBackPress) {
            Icon(Icons.Filled.ChevronLeft, null)
        }

        Text(
            suiteName,
            modifier = Modifier.padding(start = S.x4),
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.weight(1f))

        AnimatedVisibility(
            visible = shouldShowReviewButton()
        ) {
            Button(
                onClick = { openResult() },
                modifier = Modifier.padding(horizontal = Spacing.x4)
            ) {
                Text("Review")
            }
        }

        IconButton(
            onClick = { toPrevQuestion() },
            enabled = questionIdx > 0
        ) {
            Icon(Icons.Filled.ArrowBack, null)
        }

        Text(
            "${questionIdx + 1} / $questionCount",
            modifier = Modifier.width(90.dp).padding(horizontal = S.x4),
            textAlign = TextAlign.Center
        )

        IconButton(
            onClick = { toNextQuestion() },
            enabled = questionIdx < questionCount - 1
        ) {
            Icon(Icons.Filled.ArrowForward, null)
        }
    }

    Divider(Modifier.fillMaxWidth())
}

private const val DEFAULT_ANSWER = -1

@Composable
private fun ColumnScope.TestSuiteComp(
    questions: List<Question>,
    userAnswers: Map<Long, Int>,
    questionIdx: Int,
    getImageFilePath: (String?) -> String?,
    onAnswer: (questionId: Long, answerIdx: Int) -> Unit,
    modifier: Modifier = Modifier,
    toNextQuestion: (() -> Unit)? = null
) {
    val question = questions[questionIdx]

    QuestionComp(
        questionIdx,
        question,
        getImageFilePath = getImageFilePath,
        onAnswer = onAnswer,
        modifier = modifier.padding(horizontal = S.x4),
        preselect = userAnswers[question.id] ?: DEFAULT_ANSWER,
        toNextQuestion = if (questionIdx != questions.lastIndex) toNextQuestion else null
    )

    Spacer(Modifier.weight(1f))
}