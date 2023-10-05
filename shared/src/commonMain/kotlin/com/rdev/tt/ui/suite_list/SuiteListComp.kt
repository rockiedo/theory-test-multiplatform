package com.rdev.tt.ui.suite_list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rdev.tt._utils.Spacing
import com.rdev.tt._utils.koinViewModel
import com.rdev.tt.core_model.Suite

@Composable
fun SuiteListScreen(
    modifier: Modifier = Modifier,
    onSelectSuite: (Suite) -> Unit,
    viewModel: SuiteListViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    if (state !is SuiteListUiState.Content) {
        Box(modifier) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
        return
    }

    (state as? SuiteListUiState.Content)?.let {
        Column(modifier) {
            Text(
                "${it.learnedQuestionCount} / ${it.allQuestionCount}",
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = Spacing.x4),
                style = MaterialTheme.typography.headlineSmall
            )

            Divider(Modifier.fillMaxWidth())

            SuiteListComp(it.suites, Modifier.fillMaxWidth()) { suite -> onSelectSuite(suite) }
        }
    }
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3WindowSizeClassApi::class
)
@Composable
private fun SuiteListComp(
    suites: List<SuiteItem>,
    modifier: Modifier = Modifier,
    onClick: (Suite) -> Unit = {},
) {
    val isCompactScreen = calculateWindowSizeClass().widthSizeClass == WindowWidthSizeClass.Compact
    val itemShape = RoundedCornerShape(16)

    LazyColumn(modifier) {
        if (!isCompactScreen) {
            item { Spacer(Modifier.height(Spacing.x4)) }
        }

        items(suites.size) { index ->
            val item = suites[index]

            ListItem(
                overlineContent = {
                    if (item.learnedQuestionCount == item.questionCount) {
                        Badge(containerColor = Color(0xFF6BDF6A)) {
                            Text("Complete")
                        }
                    }
                },
                headlineContent = { Text(item.suite.name) },
                trailingContent = {
                    Text("Learned ${item.learnedQuestionCount} / ${item.questionCount}")
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
}