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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import kotlin.math.abs

/**
 * M3E slider card. [index]/[count] place it inside a visual group of stacked
 * sliders so corners and gaps match the segmented list rows around it. Values
 * snap to [snapPoints] when released within [snapThreshold] (fraction of range).
 */
@Composable
fun StyledSlider(
    label: String? = null,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    snapPoints: List<Float> = emptyList(),
    snapThreshold: Float = 0.05f,
    startIcon: ImageVector? = null,
    endIcon: ImageVector? = null,
    startLabel: String? = null,
    endLabel: String? = null,
    @Suppress("UNUSED_PARAMETER") independent: Boolean = false,
    description: String? = null,
    enabled: Boolean = true,
    index: Int = 0,
    count: Int = 1
) {
    val haptics = LocalHapticFeedback.current
    val span = valueRange.endInclusive - valueRange.start
    val shape = groupItemShape(index, count)

    Column(modifier = Modifier.fillMaxWidth()) {
        label?.let { SectionHeader(it) }
        Surface(
            shape = shape,
            color = listContainerColor,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    startIcon?.let {
                        Icon(it, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.size(12.dp))
                    }
                    Slider(
                        value = value,
                        onValueChange = { raw ->
                            val snapped = snapIfClose(raw, snapPoints, snapThreshold * span)
                            if (snapped != raw && snapped != value) {
                                haptics.performHapticFeedback(HapticFeedbackType.SegmentTick)
                            }
                            onValueChange(snapped)
                        },
                        valueRange = valueRange,
                        enabled = enabled,
                        modifier = Modifier.weight(1f)
                    )
                    endIcon?.let {
                        Spacer(Modifier.size(12.dp))
                        Icon(it, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(24.dp))
                    }
                }
                if (startLabel != null || endLabel != null) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        startLabel?.let {
                            Text(it, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(Modifier.weight(1f))
                        endLabel?.let {
                            Text(it, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
        if (index + 1 != count) Spacer(Modifier.size(ListItemDefaults.SegmentedGap))
    }
}

private fun snapIfClose(value: Float, points: List<Float>, threshold: Float): Float {
    val nearest = points.minByOrNull { abs(it - value) } ?: return value
    return if (abs(nearest - value) <= threshold) nearest else value
}
