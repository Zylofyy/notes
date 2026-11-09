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

import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

/** Icon button with M3E press-morphing shapes. */
@Composable
fun StyledIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    materialButtonStyle: MaterialButtonStyle = MaterialButtonStyle.Tonal
) {
    val shapes = IconButtonDefaults.shapes()
    val content: @Composable () -> Unit = {
        Icon(icon, contentDescription = contentDescription, modifier = Modifier.size(IconButtonDefaults.mediumIconSize))
    }
    val sized = modifier.size(IconButtonDefaults.mediumContainerSize())
    when (materialButtonStyle) {
        MaterialButtonStyle.Filled -> FilledIconButton(onClick = onClick, shapes = shapes, modifier = sized, enabled = enabled, content = content)
        MaterialButtonStyle.Tonal -> FilledTonalIconButton(onClick = onClick, shapes = shapes, modifier = sized, enabled = enabled, content = content)
        MaterialButtonStyle.Outlined -> OutlinedIconButton(onClick = onClick, shapes = shapes, modifier = sized, enabled = enabled, content = content)
        MaterialButtonStyle.Normal -> IconButton(onClick = onClick, shapes = shapes, modifier = sized, enabled = enabled, content = content)
    }
}
