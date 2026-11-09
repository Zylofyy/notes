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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.HeadsetOff
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import me.kavishdevar.librepods.R
import me.kavishdevar.librepods.data.NoiseControlMode

private data class ListeningOption(val mode: NoiseControlMode, val label: Int, val icon: Painter)

/**
 * Listening mode picker: an M3E connected button group. The selected mode morphs
 * to a fully rounded shape and fills with the primary color.
 */
@Composable
fun NoiseControlSettings(
    showOffListeningMode: Boolean,
    noiseControlModeValue: Int,
    onNoiseControlModeChanged: (Int) -> Unit
) {
    val haptics = LocalHapticFeedback.current
    val offIcon = rememberVectorPainter(Icons.Rounded.HeadsetOff)
    val transparencyIcon = rememberBitmapPainter(R.drawable.transparency)
    val adaptiveIcon = rememberBitmapPainter(R.drawable.adaptive)
    val ancIcon = rememberBitmapPainter(R.drawable.noise_cancellation)

    val options = buildList {
        if (showOffListeningMode) add(ListeningOption(NoiseControlMode.OFF, R.string.off, offIcon))
        add(ListeningOption(NoiseControlMode.TRANSPARENCY, R.string.transparency, transparencyIcon))
        add(ListeningOption(NoiseControlMode.ADAPTIVE, R.string.adaptive, adaptiveIcon))
        add(ListeningOption(NoiseControlMode.NOISE_CANCELLATION, R.string.noise_cancellation, ancIcon))
    }

    val selectedMode = NoiseControlMode.entries[
        (noiseControlModeValue - 1).coerceIn(0, NoiseControlMode.entries.lastIndex)
    ]

    Column {
        SectionHeader(stringResource(R.string.listening_mode))
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = listContainerColor,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
            ) {
                options.forEachIndexed { index, option ->
                    val checked = selectedMode == option.mode
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f),
                    ) {
                        ToggleButton(
                            checked = checked,
                            onCheckedChange = {
                                if (it) {
                                    haptics.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
                                    onNoiseControlModeChanged(option.mode.ordinal + 1)
                                }
                            },
                            shapes = when (index) {
                                0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                            },
                            colors = ToggleButtonDefaults.toggleButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                        ) {
                            Icon(
                                painter = option.icon,
                                contentDescription = stringResource(option.label),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Text(
                            text = stringResource(option.label),
                            style = if (checked) MaterialTheme.typography.labelMediumEmphasized
                            else MaterialTheme.typography.labelMedium,
                            color = if (checked) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun rememberBitmapPainter(id: Int): Painter {
    val bitmap = ImageBitmap.imageResource(id)
    return remember(bitmap) { BitmapPainter(bitmap) }
}
