package com.rdev.tt.ui.test_result

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rdev.tt.AppNavItem
import com.rdev.tt._utils.Spacing
import com.rdev.tt.ui.components.IndexedLazyColumn
import com.rdev.tt.ui.components.rememberIndexedLazyListState
import com.rdev.tt.ui.question.renderQuestion
import kotlinx.coroutines.launch

private const val DEFAULT_ANSWER = -1

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
@Composable
fun TestResultScreen(
    navItem: AppNavItem.TestResult,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (suiteName, questions, userAnswers) = navItem

    val wrongCount = remember {
        questions.count { userAnswers[it.id] != it.answerIdx }
    }
    val indexedState = rememberIndexedLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var isDropdownMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier,
        topBar = {
            Surface(shadowElevation = 8.dp) {
                TopAppBar(
                    title = {
                        Text(
                            suiteName,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(end = Spacing.x2)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onClose) {
                            Icon(Icons.Filled.Close, null)
                        }
                    },
                    actions = {
                        TextButton(
                            onClick = { isDropdownMenuExpanded = true }
                        ) {
                            Text(
                                if (wrongCount > 0) {
                                    "$wrongCount WRONG"
                                } else {
                                    "WELL DONE"
                                }
                            )
                        }

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
                                        coroutineScope.launch {
                                            indexedState.itemIndexMapping[question.id]?.let {
                                                indexedState.listState.animateScrollToItem(it)
                                            }
                                        }
                                    },
                                    trailingIcon = {
                                        if (userAnswers[question.id] == question.answerIdx) {
                                            return@DropdownMenuItem
                                        }

                                        Icon(
                                            Icons.Filled.Close,
                                            null,
                                            Modifier.size(20.dp)
                                        )
                                    }
                                )
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        IndexedLazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            state = indexedState
        ) {
            questions.forEachIndexed { index, question ->
                val isWrongAnswer = userAnswers[question.id] != question.answerIdx

                renderQuestion(
                    questionIndex = index,
                    question = question,
                    selection = userAnswers[question.id] ?: DEFAULT_ANSWER,
                    isDoingTest = false,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.x4),
                    questionColor = { getQuestionColor(isWrongAnswer) },
                    onAnswer = { _, _ -> },
                )

                if (index < questions.lastIndex) {
                    stickyHeader {
                        Divider(Modifier.fillMaxWidth().padding(top = Spacing.x2))
                    }
                }

                item {
                    val space = if (index < questions.lastIndex) {
                        Spacing.x4
                    } else {
                        Spacing.x8
                    }
                    Spacer(Modifier.height(space))
                }
            }
        }
    }
}

@Composable
fun getQuestionColor(isWrongAnswer: Boolean): Color {
    return when {
        !isWrongAnswer -> Color.Unspecified
        isSystemInDarkTheme() -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.error
    }
}