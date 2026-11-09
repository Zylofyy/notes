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

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

enum class MaterialButtonStyle {
    Tonal,
    Normal,
    Outlined,
    Filled
}

/**
 * Medium-size M3E button (56dp) whose corners morph on press. [materialButtonStyle]
 * picks the emphasis level: Filled > Tonal > Outlined > Normal (text).
 */
@Composable
fun StyledButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    materialButtonStyle: MaterialButtonStyle = MaterialButtonStyle.Tonal,
    content: @Composable RowScope.() -> Unit,
) {
    val height = ButtonDefaults.MediumContainerHeight
    val shapes = ButtonDefaults.shapesFor(height)
    val padding = ButtonDefaults.contentPaddingFor(height)
    val sizedModifier = modifier.heightIn(min = height)
    val styledContent: @Composable RowScope.() -> Unit = {
        ProvideTextStyle(ButtonDefaults.textStyleFor(height)) { content() }
    }
    when (materialButtonStyle) {
        MaterialButtonStyle.Filled -> Button(
            onClick = onClick, shapes = shapes, modifier = sizedModifier,
            enabled = enabled, contentPadding = padding, content = styledContent
        )
        MaterialButtonStyle.Tonal -> FilledTonalButton(
            onClick = onClick, shapes = shapes, modifier = sizedModifier,
            enabled = enabled, contentPadding = padding, content = styledContent
        )
        MaterialButtonStyle.Outlined -> OutlinedButton(
            onClick = onClick, shapes = shapes, modifier = sizedModifier,
            enabled = enabled, contentPadding = padding, content = styledContent
        )
        MaterialButtonStyle.Normal -> TextButton(
            onClick = onClick, shapes = shapes, modifier = sizedModifier,
            enabled = enabled, contentPadding = padding, content = styledContent
        )
    }
}
