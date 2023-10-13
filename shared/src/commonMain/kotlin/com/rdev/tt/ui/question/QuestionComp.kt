package com.rdev.tt.ui.question

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.rdev.tt._utils.Spacing
import com.rdev.tt._utils.isValidImageName
import com.rdev.tt.core_model.Question
import com.rdev.tt.ui.components.CustomImage

private val indexers = listOf("A.", "B.", "C.", "D.")

@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.renderQuestion(
    questionIndex: Int,
    question: Question,
    selection: Int,
    isDoingTest: Boolean,
    onAnswer: (questionId: Long, answerIdx: Int) -> Unit,
    modifier: Modifier = Modifier,
    questionColor: @Composable () -> Color = { Color.Unspecified },
) {
    val hasUserInput = selection != -1

    stickyHeader(key = question.id) {
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
            CustomImage(
                question.image!!,
                Modifier.fillMaxWidth()
                    .height(250.dp)
                    .padding(horizontal = Spacing.x4)
                    .padding(top = Spacing.x4)
            )
        }
    }

    item {
        Spacer(Modifier.height(Spacing.x4))
    }

    itemsIndexed(question.choices) { index, choice ->
        if (isDoingTest) {
            TestingChoiceComp(
                indexer = indexers[index],
                content = choice,
                isSelected = index == selection,
                onClick = { onAnswer(question.id, index) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.x4)
            )
        } else {
            ChoiceComp(
                indexer = indexers[index],
                content = choice,
                choiceState = when {
                    index == question.answerIdx -> ChoiceState.Correct
                    index == selection && index != question.answerIdx -> ChoiceState.Incorrect
                    else -> ChoiceState.Neutral
                },
                shouldHighlight = hasUserInput,
                onClick = { onAnswer(question.id, index) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.x4)
            )
        }

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
) {
    val baseScheme = MaterialTheme.colorScheme
    val borderColor = baseScheme.onBackground.copy(alpha = 0.75f)
    val shape = RoundedCornerShape(16)

    val (backgroundColor, onBackgroundColor, strokeColor) = when {
        shouldHighlight && choiceState == ChoiceState.Correct -> {
            Triple(
                baseScheme.primaryContainer,
                baseScheme.onPrimaryContainer,
                baseScheme.onPrimaryContainer
            )
        }

        shouldHighlight && choiceState == ChoiceState.Incorrect -> {
            Triple(
                baseScheme.errorContainer,
                baseScheme.onErrorContainer,
                baseScheme.onErrorContainer
            )
        }

        else -> {
            Triple(baseScheme.background, baseScheme.onBackground, borderColor)
        }
    }

    var cascadingModifier = modifier.clip(shape).border(
        width = 0.5.dp, shape = shape, color = strokeColor
    )

    if (!shouldHighlight) {
        cascadingModifier = cascadingModifier
            .clickable { onClick() }
    }

    ListItem(
        headlineContent = { Text(content) },
        overlineContent = { Text(indexer, style = MaterialTheme.typography.labelMedium) },
        colors = ListItemDefaults.colors(
            containerColor = backgroundColor,
            headlineColor = onBackgroundColor,
            overlineColor = onBackgroundColor
        ),
        modifier = cascadingModifier
    )
}

@Composable
private fun TestingChoiceComp(
    indexer: String,
    content: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val baseScheme = MaterialTheme.colorScheme
    val baseBorderColor = baseScheme.onBackground.copy(alpha = 0.75f)
    val shape = RoundedCornerShape(16)

    val (backgroundColor, onBackgroundColor, borderColor) = if (isSelected) {
        Triple(
            baseScheme.tertiaryContainer,
            baseScheme.onTertiaryContainer,
            baseScheme.onTertiaryContainer
        )
    } else {
        Triple(baseScheme.background, baseScheme.onBackground, baseBorderColor)
    }

    val cascadingModifier = modifier.clip(shape)
        .border(width = 0.5.dp, shape = shape, color = borderColor)
        .clickable { onClick() }

    ListItem(
        headlineContent = { Text(content) },
        overlineContent = { Text(indexer, style = MaterialTheme.typography.labelMedium) },
        colors = ListItemDefaults.colors(
            containerColor = backgroundColor,
            headlineColor = onBackgroundColor,
            overlineColor = onBackgroundColor
        ),
        modifier = cascadingModifier
    )
}