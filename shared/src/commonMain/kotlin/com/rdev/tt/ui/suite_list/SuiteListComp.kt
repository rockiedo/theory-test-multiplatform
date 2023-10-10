package com.rdev.tt.ui.suite_list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.rdev.tt._utils.ExtendedColorScheme
import com.rdev.tt._utils.Spacing
import com.rdev.tt._utils.koinViewModel
import com.rdev.tt.core_model.Suite

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3WindowSizeClassApi::class
)
@Composable
fun SuiteListScreen(
    modifier: Modifier = Modifier,
    onSelectSuite: (Suite) -> Unit,
    viewModel: SuiteListViewModel = koinViewModel(SuiteListViewModel::class)
) {
    val isCompactScreen = calculateWindowSizeClass().widthSizeClass == WindowWidthSizeClass.Compact
    val isDarkTheme = isSystemInDarkTheme()
    val state by viewModel.uiState.collectAsState()

    if (state !is SuiteListUiState.Content) {
        Box(modifier) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
        return
    }

    (state as? SuiteListUiState.Content)?.let { content ->
        Scaffold(
            modifier,
            topBar = {
                Surface(shadowElevation = 8.dp) {
                    CenterAlignedTopAppBar(
                        title = { Text("Theory Test") }
                    )
                }
            }
        ) { innerPadding ->
            LazyColumn(
                Modifier.fillMaxWidth().padding(innerPadding)
            ) {
                if (content.visitedQuestionCount > 0) {
                    renderStatsCard(
                        content.visitedQuestionCount,
                        content.learnedQuestionCount,
                        content.allQuestionCount,
                        Modifier.fillMaxWidth()
                            .padding(horizontal = Spacing.x4)
                            .padding(top = Spacing.x4)
                    )
                }

                renderSuiteList(
                    content.suites,
                    isCompactScreen,
                    isDarkTheme
                ) { suite -> onSelectSuite(suite) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private fun LazyListScope.renderSuiteList(
    suites: List<SuiteItem>,
    isCompactScreen: Boolean,
    isDarkTheme: Boolean,
    onClick: (Suite) -> Unit = {},
) {
    val itemShape = RoundedCornerShape(16)
    val customScheme = if (isDarkTheme) ExtendedColorScheme.Light else ExtendedColorScheme.Dark

    if (!isCompactScreen) {
        item { Spacer(Modifier.height(Spacing.x4)) }
    }

    items(suites.size) { index ->
        val item = suites[index]

        ListItem(
            overlineContent = {
                if (item.learnedQuestionCount == item.questionCount) {
                    Badge(
                        containerColor = customScheme.background,
                        contentColor = customScheme.onBackground
                    ) {
                        Text("Complete")
                    }
                }
            },
            headlineContent = {
                Row(verticalAlignment = Alignment.Top) {
                    Text("${index + 1}.")

                    Text(
                        item.suite.name,
                        modifier = Modifier.weight(1f).padding(start = Spacing.x2)
                    )
                }
            },
            trailingContent = {
                Text("${item.learnedQuestionCount} / ${item.questionCount}")
            },
            modifier = Modifier
                .padding(vertical = Spacing.x2)
                .clip(itemShape)
                .clickable { onClick(item.suite) }
        )

        if (index < suites.size - 1) {
            Divider(thickness = 1.dp, modifier = Modifier.padding(horizontal = Spacing.x4))
        }
    }

    item { Spacer(Modifier.height(Spacing.x4)) }
}

private fun LazyListScope.renderStatsCard(
    visitedCount: Int,
    learnedCount: Int,
    total: Int,
    modifier: Modifier = Modifier,
    onReviewWrongAnswers: () -> Unit = {}
) {
    val childModifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.x4)

    item {
        OutlinedCard(modifier) {
            Text(
                "Learned: $learnedCount / $total",
                modifier = childModifier.padding(top = Spacing.x4),
                style = MaterialTheme.typography.titleMedium
            )

            LinearProgressIndicator(
                progress = learnedCount * 1f / total,
                modifier = childModifier.padding(top = Spacing.x4)
            )

            if (visitedCount > learnedCount) {
                TextButton(
                    onClick = onReviewWrongAnswers,
                    modifier = childModifier.padding(bottom = Spacing.x).align(Alignment.End),
                ) {
                    Text("Review wrong answers")
                }
            } else {
                Spacer(Modifier.height(Spacing.x4))
            }
        }
    }
}