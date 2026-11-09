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

import androidx.compose.runtime.getValue
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp

/**
 * Stand-alone switch row. With [header] = true it becomes the large "primary switch"
 * used at the top of feature screens (Hearing Aid, Head Gestures, ...): a full pill that
 * fills with primaryContainer when on.
 */
@Composable
fun StyledToggle(
    title: String? = null,
    label: String,
    description: String? = null,
    checked: Boolean = false,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit,
    header: Boolean = false,
    icon: ImageVector? = null,
) {
    if (header) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            title?.let { SectionHeader(it) }
            PrimarySwitchRow(label, checked, enabled, onCheckedChange)
            description?.let { SectionFooter(it) }
        }
    } else {
        StyledList(title = title) {
            StyledToggle(
                label = label,
                description = description,
                checked = checked,
                enabled = enabled,
                onCheckedChange = onCheckedChange,
                icon = icon
            )
        }
    }
}

/** A switch row inside a [StyledList] group; the whole row toggles. */
@Composable
fun StyledListScope.StyledToggle(
    label: String,
    description: String? = null,
    checked: Boolean = false,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit,
    icon: ImageVector? = null,
) {
    item { index, count ->
        ToggleRow(
            index = index,
            count = count,
            label = label,
            description = description,
            checked = checked,
            enabled = enabled,
            onCheckedChange = onCheckedChange,
            icon = icon
        )
    }
}

@Composable
internal fun ToggleRow(
    index: Int,
    count: Int,
    label: String,
    description: String?,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: ImageVector?,
) {
    val haptics = LocalHapticFeedback.current
    SegmentedListItem(
        checked = checked,
        onCheckedChange = {
            haptics.performHapticFeedback(if (it) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff)
            onCheckedChange(it)
        },
        // A switched-on row keeps its group shape; only press/focus morph.
        shapes = groupItemShapes(index, count, keepShapeWhenSelected = true),
        modifier = Modifier.heightIn(min = 64.dp),
        enabled = enabled,
        leadingContent = icon?.let { { ListIcon(it) } },
        trailingContent = { ExpressiveSwitch(checked = checked, enabled = enabled) },
        supportingContent = description?.let { { Text(it) } },
        verticalAlignment = Alignment.CenterVertically,
        colors = ListItemDefaults.segmentedColors(
            containerColor = listContainerColor,
            selectedContainerColor = listContainerColor,
        ),
        content = { Text(label, style = MaterialTheme.typography.bodyLarge) }
    )
}

/** Decorative M3 switch with check / close thumb icons; the parent row owns the click. */
@Composable
fun ExpressiveSwitch(
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: ((Boolean) -> Unit)? = null,
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        enabled = enabled,
        thumbContent = {
            Icon(
                imageVector = if (checked) Icons.Rounded.Check else Icons.Rounded.Close,
                contentDescription = null,
                modifier = Modifier.size(SwitchDefaults.IconSize)
            )
        }
    )
}

@Composable
private fun PrimarySwitchRow(
    label: String,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val haptics = LocalHapticFeedback.current
    val container by animateColorAsState(
        if (checked) MaterialTheme.colorScheme.primaryContainer else listContainerColor,
        label = "primarySwitchContainer"
    )
    val content by animateColorAsState(
        if (checked) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
        label = "primarySwitchContent"
    )
    SegmentedListItem(
        checked = checked,
        onCheckedChange = {
            haptics.performHapticFeedback(if (it) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff)
            onCheckedChange(it)
        },
        shapes = ListItemDefaults.shapes(
            shape = CircleShape,
            selectedShape = CircleShape,
            pressedShape = groupItemShape(0, 1),
            focusedShape = CircleShape,
            hoveredShape = CircleShape,
            draggedShape = CircleShape,
        ),
        modifier = Modifier.heightIn(min = 80.dp),
        enabled = enabled,
        trailingContent = { ExpressiveSwitch(checked = checked, enabled = enabled) },
        verticalAlignment = Alignment.CenterVertically,
        colors = ListItemDefaults.segmentedColors(
            containerColor = container,
            selectedContainerColor = container,
            contentColor = content,
            selectedContentColor = content,
        ),
        content = {
            Text(
                text = label,
                style = MaterialTheme.typography.titleLargeEmphasized,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    )
}
