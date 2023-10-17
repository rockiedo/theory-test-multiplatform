package com.rdev.tt.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rdev.tt._utils.Spacing
import com.rdev.tt.core_model.Category

private data class DrawerItem(
    val displayName: String,
    val category: @Category String
)

private val items = listOf(
    DrawerItem("Basic Theory Test", Category.BTT),
    DrawerItem("Final Theory Test", Category.FTT),
)

@Composable
fun HomeDrawerComp(
    selectedCategory: @Category String,
    onSelect: (@Category String) -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(modifier) {
        Text(
            "Categories",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = Spacing.x4, horizontal = Spacing.x7)
        )

        items.forEach { item ->
            NavigationDrawerItem(
                label = {
                    Text(
                        item.displayName,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                selected = item.category == selectedCategory,
                onClick = { onSelect(item.category) },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
    }
}