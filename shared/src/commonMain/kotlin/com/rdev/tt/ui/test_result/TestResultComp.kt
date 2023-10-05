package com.rdev.tt.ui.test_result

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rdev.tt.AppNavItem
import com.rdev.tt._utils.Spacing
import com.rdev.tt.ui.question.renderQuestion

private const val DEFAULT_ANSWER = -1

@OptIn(
    ExperimentalMaterial3WindowSizeClassApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun TestResultScreen(
    navItem: AppNavItem.TestResult,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCompactScreen = calculateWindowSizeClass().widthSizeClass == WindowWidthSizeClass.Compact

    Scaffold(
        modifier,
        topBar = {
            Surface(shadowElevation = 2.dp) {
                TopAppBar(
                    title = {
                        Text(
                            navItem.suiteName,
                            modifier = Modifier.padding(start = Spacing.x4),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onClose) {
                            Icon(Icons.Filled.Close, null)
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(Modifier.fillMaxSize().padding(innerPadding)) {
            item {
                if (!isCompactScreen) {
                    Spacer(Modifier.height(Spacing.x4))
                }
            }

            navItem.questions.forEachIndexed { index, question ->
                this@LazyColumn.renderQuestion(
                    questionIndex = index,
                    question = question,
                    category = navItem.category,
                    selection = navItem.userAnswers[question.id] ?: DEFAULT_ANSWER,
                    isCompactScreen = isCompactScreen,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.x4),
                    onAnswer = { _, _ -> },
                )

                if (index < navItem.questions.lastIndex) {
                    item {
                        Divider(Modifier.fillMaxWidth().padding(top = Spacing.x2))
                    }
                }

                item {
                    val space = if (index < navItem.questions.lastIndex) {
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