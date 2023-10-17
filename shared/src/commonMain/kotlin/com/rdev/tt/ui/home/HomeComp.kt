package com.rdev.tt.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.rdev.tt._utils.Spacing
import com.rdev.tt.core_model.Category
import com.rdev.tt.core_model.Suite

private data class HomeTab(
    val displayName: String,
    val category: @Category String
)

private val homeTabs = listOf(
    HomeTab("Lessons", Category.LESSON),
    HomeTab("Tests", Category.TEST)
)

@Composable
fun HomeComp(
    content: HomeUiState.Content,
    openSuite: (Suite, Boolean) -> Unit,
    reviewWronglyAnsweredQuestions: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    var selectedTab by rememberSaveable { mutableStateOf(0) }
    val suiteList = remember(selectedTab, content) {
        val tab = homeTabs[selectedTab]
        content.suites.filter { it.suite.categories.contains(tab.category) }
    }

    LazyColumn(modifier) {
        if (content.visitedQuestionCount > 0) {
            renderStatsCard(
                content.categoryDisplay,
                content.visitedQuestionCount,
                content.learnedQuestionCount,
                content.allQuestionCount,
                Modifier.fillMaxWidth()
                    .padding(horizontal = Spacing.x4)
                    .padding(top = Spacing.x4)
            ) {
                reviewWronglyAnsweredQuestions()
            }
        } else {
            renderWelcomeCard(
                "Basic Theory Test (BTT)",
                Modifier.fillMaxWidth()
                    .padding(horizontal = Spacing.x4)
                    .padding(top = Spacing.x4)
            )
        }

        item { Spacer(Modifier.height(Spacing.x4)) }

        renderTabs(
            selectedTab = selectedTab,
            modifier = Modifier.fillMaxWidth()
        ) {
            selectedTab = it
        }

        renderSuiteList(suiteList, colorScheme) { suite ->
            openSuite(suite, homeTabs[selectedTab].category == Category.TEST)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.renderTabs(
    selectedTab: Int,
    modifier: Modifier = Modifier,
    onTabClick: (Int) -> Unit = {}
) {
    stickyHeader {
        TabRow(
            selectedTabIndex = selectedTab,
            modifier = modifier
        ) {
            homeTabs.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { onTabClick(index) },
                    text = { Text(tab.displayName) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private fun LazyListScope.renderSuiteList(
    suites: List<SuiteItem>,
    colorScheme: ColorScheme,
    onClick: (Suite) -> Unit = {},
) {
    val itemShape = RoundedCornerShape(16)

    items(suites.size) { index ->
        val item = suites[index]

        ListItem(
            overlineContent = {
                if (item.learnedQuestionCount == item.questionCount) {
                    Badge(
                        containerColor = colorScheme.primary,
                        contentColor = colorScheme.onPrimary
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

private fun LazyListScope.renderWelcomeCard(
    title: String,
    modifier: Modifier = Modifier
) {
    item {
        Card(modifier) {
            Text(
                title,
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = Spacing.x4)
                    .padding(top = Spacing.x4),
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                "Looks like you haven't started. Let's start learning!",
                modifier = Modifier.fillMaxWidth().padding(Spacing.x4),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private fun LazyListScope.renderStatsCard(
    categoryDisplay: String,
    visitedCount: Int,
    learnedCount: Int,
    total: Int,
    modifier: Modifier = Modifier,
    onReviewWrongAnswers: () -> Unit = {}
) {
    val childModifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.x4)

    item {
        Card(modifier) {
            Row(
                modifier = childModifier.padding(top = Spacing.x4),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    categoryDisplay,
                    modifier = Modifier.padding(end = Spacing.x4).weight(1f),
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    "$learnedCount / $total",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            LinearProgressIndicator(
                progress = learnedCount * 1f / total,
                modifier = childModifier.padding(top = Spacing.x4),
                color = MaterialTheme.colorScheme.tertiary,
                trackColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f),
                strokeCap = StrokeCap.Round
            )

            if (visitedCount > learnedCount) {
                TextButton(
                    onClick = onReviewWrongAnswers,
                    modifier = childModifier.padding(bottom = Spacing.x).align(Alignment.End),
                ) {
                    Text("Revise wrong answers")
                }
            } else {
                Text(
                    "You've been doing great. Keep it going!",
                    modifier = childModifier.padding(vertical = Spacing.x4),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}