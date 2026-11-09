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

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp

@Composable
fun StyledInputField(
    inputState: TextFieldState,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    label: String? = null,
    singleLine: Boolean = true,
) {
    OutlinedTextField(
        state = inputState,
        modifier = modifier
            .fillMaxWidth()
            .then(if (singleLine) Modifier else Modifier.heightIn(min = 160.dp))
            .focusRequester(focusRequester),
        label = label?.let { { Text(it) } },
        placeholder = if (placeholder.isNotEmpty()) { { Text(placeholder) } } else null,
        lineLimits = if (singleLine) TextFieldLineLimits.SingleLine else TextFieldLineLimits.MultiLine(minHeightInLines = 5),
        shape = MaterialTheme.shapes.large,
        textStyle = MaterialTheme.typography.bodyLarge,
    )
}
