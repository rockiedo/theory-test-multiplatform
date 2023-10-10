package com.rdev.tt.ui.question

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rdev.tt._utils.ExtendedColorScheme
import com.rdev.tt._utils.Spacing
import com.rdev.tt._utils.isValidImageName
import com.rdev.tt.core_model.Category
import com.rdev.tt.core_model.Question
import com.rdev.tt.ui.components.CustomImage

private val indexers = listOf("A.", "B.", "C.", "D.")

@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.renderQuestion(
    questionIndex: Int,
    question: Question,
    category: @Category String,
    selection: Int,
    isCompactScreen: Boolean,
    onAnswer: (questionId: Long, answerIdx: Int) -> Unit,
    modifier: Modifier = Modifier,
    questionColor: @Composable () -> Color = { Color.Unspecified },
    toNextQuestion: (() -> Unit)? = null
) {
    val hasUserInput = selection != -1

    stickyHeader {
        Surface(
            Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            Text(
                "${questionIndex + 1}. ${question.question}",
                style = MaterialTheme.typography.titleMedium,
                modifier = modifier.padding(top = Spacing.x4, bottom = Spacing.x),
                color = questionColor()
            )
        }
    }

    if (isValidImageName(question.image)) {
        item {
            if (isCompactScreen) {
                Spacer(Modifier.height(Spacing.x4))
            } else {
                Spacer(Modifier.height(Spacing.x6))
            }
        }

        item {
            CustomImage(
                question.image!!,
                category,
                Modifier.fillMaxWidth().height(250.dp).padding(horizontal = Spacing.x4)
            )
        }
    }

    item {
        if (isCompactScreen) {
            Spacer(Modifier.height(Spacing.x4))
        } else {
            Spacer(Modifier.height(Spacing.x6))
        }
    }

    itemsIndexed(question.choices) { index, choice ->
        ChoiceComp(
            indexer = indexers[index],
            content = choice,
            choiceState = when {
                index == question.answerIdx -> ChoiceState.Correct
                index == selection && index != question.answerIdx -> ChoiceState.Incorrect
                else -> ChoiceState.Neutral
            },
            shouldHighlight = hasUserInput,
            onClick = {
                onAnswer(question.id, index)
            },
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = Spacing.x4),
            toNextQuestion = toNextQuestion
        )

        if (index < question.choices.lastIndex) {
            Spacer(Modifier.height(Spacing.x4))
        }
    }

    item { Spacer(modifier.height(Spacing.x6)) }
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
    val baseScheme = MaterialTheme.colorScheme
    val borderColor = baseScheme.onBackground.copy(alpha = 0.75f)

    val customScheme = if (isSystemInDarkTheme()) {
        ExtendedColorScheme.Light
    } else {
        ExtendedColorScheme.Dark
    }
    val shape = RoundedCornerShape(16)

    val (backgroundColor, onBackgroundColor, strokeColor) = when {
        shouldHighlight && choiceState == ChoiceState.Correct -> {
            Triple(customScheme.background, customScheme.onBackground, null)
        }

        shouldHighlight && choiceState == ChoiceState.Incorrect -> {
            Triple(baseScheme.error, baseScheme.onError, null)
        }

        else -> {
            Triple(baseScheme.background, baseScheme.onBackground, borderColor)
        }
    }

    var cascadingModifier = modifier.clip(shape)

    if (strokeColor != null) {
        cascadingModifier = cascadingModifier.border(
            width = 0.5.dp, shape = shape, color = strokeColor
        )
    }

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
        colors = ListItemDefaults.colors(
            containerColor = backgroundColor,
            headlineColor = onBackgroundColor,
            overlineColor = onBackgroundColor
        ),
        modifier = cascadingModifier
    )
}