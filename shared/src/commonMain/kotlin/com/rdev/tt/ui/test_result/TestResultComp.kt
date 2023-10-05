package com.rdev.tt.ui.test_result

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.rdev.tt.AppNavItem
import com.rdev.tt._utils.Spacing
import com.rdev.tt.ui.question.renderQuestion

private const val DEFAULT_ANSWER = -1

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3WindowSizeClassApi::class
)
@Composable
fun TestResultScreen(
    navItem: AppNavItem.TestResult,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCompactScreen = calculateWindowSizeClass().widthSizeClass == WindowWidthSizeClass.Compact

    LazyColumn(modifier) {
        stickyHeader {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(Spacing.x4),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onClose) {
                    Icon(Icons.Filled.Close, null)
                }

                Text(
                    navItem.suiteName,
                    modifier = Modifier.padding(start = Spacing.x4),
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            Divider()
        }

        item { Spacer(Modifier.height(Spacing.x8)) }

        navItem.questions.forEachIndexed { index, question ->
            this@LazyColumn.renderQuestion(
                questionIndex = index,
                question = question,
                category = navItem.category,
                selection = navItem.userAnswers[question.id] ?: DEFAULT_ANSWER,
                isCompactScreen = isCompactScreen,
                modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.x8),
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