package com.rdev.tt.ui.question

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rdev.tt._utils.Spacing
import com.rdev.tt._utils.isValidImageName
import com.rdev.tt.core_model.Category
import com.rdev.tt.core_model.Question
import com.rdev.tt.ui.components.CustomImage

private val Indexers = listOf("A.", "B.", "C.", "D.")

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun QuestionComp(
    questionIndex: Int,
    question: Question,
    category: @Category String,
    onAnswer: (questionId: Long, answerIdx: Int) -> Unit,
    modifier: Modifier = Modifier,
    preselect: Int = -1,
    toNextQuestion: (() -> Unit)? = null
) {
    var selection by remember(question, preselect) { mutableStateOf(preselect) }
    val hasUserInput = selection != -1
    val windowSizeClass = calculateWindowSizeClass().widthSizeClass

    Column(
        modifier,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            "${questionIndex + 1}. ${question.question}",
            style = MaterialTheme.typography.titleMedium
        )

        if (isValidImageName(question.image)) {
            if (windowSizeClass == WindowWidthSizeClass.Compact) {
                Spacer(Modifier.height(Spacing.x4))
            } else {
                Spacer(Modifier.height(Spacing.x6))
            }

            CustomImage(
                question.image!!,
                category,
                Modifier.fillMaxWidth().height(250.dp)
            )
        }

        if (windowSizeClass == WindowWidthSizeClass.Compact) {
            Spacer(Modifier.height(Spacing.x4))
        } else {
            Spacer(Modifier.height(Spacing.x6))
        }

        question.choices.forEachIndexed { index, choice ->
            ChoiceComp(
                indexer = Indexers[index],
                content = choice,
                choiceState = when {
                    index == question.answerIdx -> ChoiceState.Correct
                    index == selection && index != question.answerIdx -> ChoiceState.Incorrect
                    else -> ChoiceState.Neutral
                },
                shouldHighlight = hasUserInput,
                onClick = {
                    selection = index
                    onAnswer(question.id, index)
                },
                modifier = Modifier.fillMaxWidth().padding(bottom = Spacing.x4),
                toNextQuestion = toNextQuestion
            )
        }

        Spacer(modifier.height(Spacing.x6))
    }
}

private enum class ChoiceState {
    Neutral, Correct, Incorrect
}

@Composable
private fun ChoiceComp(
    indexer: String,
    content: String,
    choiceState: ChoiceState,
    shouldHighlight: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    toNextQuestion: (() -> Unit)? = null
) {
    val shape = RoundedCornerShape(16)
    val backgroundColor = when {
        !shouldHighlight -> MaterialTheme.colorScheme.background
        choiceState == ChoiceState.Correct -> Color(0xFF6BDF6A)
        choiceState == ChoiceState.Incorrect -> Color(0xFFFFB4AB)
        else -> MaterialTheme.colorScheme.background
    }

    var cascadingModifier = modifier
        .clip(shape)
        .border(width = 1.dp, color = Color.LightGray, shape)

    if (!shouldHighlight) {
        cascadingModifier = cascadingModifier
            .clickable { onClick() }
    }

    ListItem(
        headlineContent = { Text(content) },
        overlineContent = { Text(indexer, style = MaterialTheme.typography.labelMedium) },
        trailingContent = {
            if (shouldHighlight && choiceState == ChoiceState.Correct && toNextQuestion != null) {
                Button(
                    onClick = toNextQuestion,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onSurface,
                        contentColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Text("Next")
                }
            }
        },
        colors = ListItemDefaults.colors(containerColor = backgroundColor),
        modifier = cascadingModifier
    )
}