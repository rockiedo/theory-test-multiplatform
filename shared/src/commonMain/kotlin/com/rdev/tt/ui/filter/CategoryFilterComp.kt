package com.rdev.tt.ui.filter

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.rdev.tt._utils.Spacing
import com.rdev.tt.core_model.Category

private data class FilterOption(
    val category: @Category String,
    val display: String
)

private val Options = listOf(
    FilterOption(Category.BTT, "Basic Theory Test (BTT)"),
    FilterOption(Category.FTT, "Final Theory Test (BTT)"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFilterComp(
    selection: @Category String,
    onSelect: (@Category String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Options.forEachIndexed { index, option ->
            val selected = option.category == selection
            var chipModifier = Modifier.weight(1f, true)

            if (index < Options.size - 1) {
                chipModifier = chipModifier.padding(end = Spacing.x4)
            }

            FilterChip(
                selected = selected,
                onClick = { onSelect(option.category) },
                label = { Text(option.display) },
                leadingIcon = {
                    if (selected) {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = null,
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                },
                modifier = chipModifier
            )
        }
    }
}