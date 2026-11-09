/*
    LibrePods - AirPods liberated from Apple’s ecosystem
    Copyright (C) 2025 LibrePods contributors

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package me.kavishdevar.librepods.presentation.components

import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Kept for source compatibility with older call sites; M3E rows are always horizontal. */
enum class ListItemOrientation {
    Horizontal,
    Vertical
}

/** A single row rendered as its own one-item group, optionally under a section [title]. */
@Composable
fun StyledListItem(
    modifier: Modifier = Modifier,
    title: String? = null,
    name: String,
    onClick: (() -> Unit)?,
    description: String? = null,
    @Suppress("UNUSED_PARAMETER") height: Dp = 64.dp,
    enabled: Boolean = true,
    @Suppress("UNUSED_PARAMETER") orientation: ListItemOrientation = ListItemOrientation.Horizontal,
    icon: ImageVector? = null,
    leadingContent: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null
) {
    StyledList(title = title) {
        item { index, count ->
            ListRow(
                modifier = modifier,
                index = index,
                count = count,
                name = name,
                description = description,
                onClick = onClick,
                enabled = enabled,
                selected = null,
                icon = icon,
                leadingContent = leadingContent,
                trailingContent = trailingContent
            )
        }
    }
}

/** A row inside a [StyledList] group. */
@Composable
fun StyledListScope.StyledListItem(
    modifier: Modifier = Modifier,
    name: String,
    onClick: (() -> Unit)? = null,
    description: String? = null,
    enabled: Boolean = onClick != null,
    @Suppress("UNUSED_PARAMETER") orientation: ListItemOrientation = ListItemOrientation.Horizontal,
    selected: Boolean? = null,
    icon: ImageVector? = null,
    leadingContent: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null
) {
    item { index, count ->
        ListRow(
            modifier = modifier,
            index = index,
            count = count,
            name = name,
            description = description,
            onClick = onClick,
            enabled = enabled,
            selected = selected,
            icon = icon,
            leadingContent = leadingContent,
            trailingContent = trailingContent
        )
    }
}

/** Segmented list colors on the app's list container, with readable "disabled" colors for static rows. */
@Composable
internal fun listRowColors(staticRow: Boolean): ListItemColors {
    val colors = ListItemDefaults.segmentedColors(
        containerColor = listContainerColor,
        selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
    )
    return if (!staticRow) colors else colors.copy(
        disabledContainerColor = colors.containerColor,
        disabledContentColor = colors.contentColor,
        disabledLeadingContentColor = colors.leadingContentColor,
        disabledTrailingContentColor = colors.trailingContentColor,
        disabledSupportingContentColor = colors.supportingContentColor,
        disabledOverlineContentColor = colors.overlineContentColor,
    )
}

@Composable
internal fun ListRow(
    modifier: Modifier,
    index: Int,
    count: Int,
    name: String,
    description: String?,
    onClick: (() -> Unit)?,
    enabled: Boolean,
    selected: Boolean?,
    icon: ImageVector?,
    leadingContent: (@Composable () -> Unit)?,
    trailingContent: (@Composable () -> Unit)?,
) {
    val staticRow = onClick == null
    val leading: (@Composable () -> Unit)? = leadingContent ?: icon?.let { { ListIcon(it) } }
    val trailing: (@Composable () -> Unit)? = trailingContent ?: when {
        staticRow -> null
        selected == true -> { { Icon(Icons.Rounded.Check, contentDescription = null) } }
        selected == null -> { { Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null) } }
        else -> null
    }
    val supporting: (@Composable () -> Unit)? = description?.let { { Text(it) } }
    val headline: @Composable () -> Unit = {
        Text(
            text = name,
            style = if (selected == true) MaterialTheme.typography.bodyLargeEmphasized
            else MaterialTheme.typography.bodyLarge
        )
    }
    val shapes = groupItemShapes(index, count)

    if (selected != null) {
        SegmentedListItem(
            selected = selected,
            onClick = onClick ?: {},
            shapes = shapes,
            modifier = modifier.heightIn(min = 64.dp),
            enabled = enabled && !staticRow,
            leadingContent = leading,
            trailingContent = trailing,
            supportingContent = supporting,
            verticalAlignment = Alignment.CenterVertically,
            colors = listRowColors(staticRow),
            content = headline
        )
    } else {
        SegmentedListItem(
            onClick = onClick ?: {},
            shapes = shapes,
            modifier = modifier.heightIn(min = 64.dp),
            enabled = enabled && !staticRow,
            leadingContent = leading,
            trailingContent = trailing,
            supportingContent = supporting,
            verticalAlignment = Alignment.CenterVertically,
            colors = listRowColors(staticRow),
            content = headline
        )
    }
}
