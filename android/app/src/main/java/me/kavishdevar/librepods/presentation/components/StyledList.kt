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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ListItemShapes
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.RoundedPolygon

/**
 * A titled group of segmented list rows (the M3E "grouped list" pattern): rows share
 * one container, get large outer corners and small inner corners, and are separated
 * by the standard segmented gap. Rows are added through [StyledListScope].
 */
@Composable
fun StyledList(
    modifier: Modifier = Modifier,
    title: String? = null,
    description: String? = null,
    content: @Composable StyledListScope.() -> Unit
) {
    val scope = StyledListScope()
    scope.content()

    Column(modifier = modifier.fillMaxWidth()) {
        title?.let { SectionHeader(it) }
        Column(verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)) {
            scope.items.forEachIndexed { index, item ->
                item(index, scope.items.size)
            }
        }
        description?.let { SectionFooter(it) }
    }
}

class StyledListScope {
    internal val items = mutableListOf<@Composable (Int, Int) -> Unit>()

    fun item(content: @Composable (index: Int, count: Int) -> Unit) {
        items += content
    }
}

/** Section label shown above a group. */
@Composable
fun SectionHeader(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.labelLargeEmphasized,
        modifier = modifier.padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 10.dp)
    )
}

/** Explanatory text shown below a group. */
@Composable
fun SectionFooter(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.bodySmall,
        modifier = modifier.padding(start = 20.dp, end = 20.dp, top = 8.dp)
    )
}

private val GroupOuterCorner = 24.dp
private val GroupInnerCorner = 6.dp

/** Static shape of row [index] in a group of [count]: large outer corners, small inner ones. */
fun groupItemShape(index: Int, count: Int): Shape = when {
    count <= 1 -> RoundedCornerShape(GroupOuterCorner)
    index == 0 -> RoundedCornerShape(GroupOuterCorner, GroupOuterCorner, GroupInnerCorner, GroupInnerCorner)
    index == count - 1 -> RoundedCornerShape(GroupInnerCorner, GroupInnerCorner, GroupOuterCorner, GroupOuterCorner)
    else -> RoundedCornerShape(GroupInnerCorner)
}

/** Row shapes for a group: interactive states morph to a fully rounded card. */
@Composable
fun groupItemShapes(index: Int, count: Int, keepShapeWhenSelected: Boolean = false): ListItemShapes {
    val shape = groupItemShape(index, count)
    val rounded = RoundedCornerShape(GroupOuterCorner)
    return ListItemDefaults.shapes(
        shape = shape,
        selectedShape = if (keepShapeWhenSelected) shape else rounded,
        pressedShape = rounded,
        focusedShape = rounded,
        hoveredShape = rounded,
        draggedShape = rounded,
    )
}

/** Container color used by every list row and card in the app. */
val listContainerColor: Color
    @Composable get() = MaterialTheme.colorScheme.surfaceBright

/**
 * Leading icon for list rows: the glyph sits on a tonal Material shape, which gives
 * each settings group the expressive "shape + color" look used across M3E.
 */
@Composable
fun ListIcon(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    shape: RoundedPolygon = MaterialShapes.Cookie9Sided,
    containerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
) {
    ListIcon(icon, modifier, shape.toShape(), containerColor, contentColor)
}

@Composable
fun ListIcon(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    shape: Shape,
    containerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(shape)
            .background(containerColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(22.dp))
    }
}
