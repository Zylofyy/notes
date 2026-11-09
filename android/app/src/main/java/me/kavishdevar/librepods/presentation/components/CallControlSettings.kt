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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.CallEnd
import androidx.compose.material.icons.rounded.MicOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import me.kavishdevar.librepods.R

@Composable
fun CallControlSettings(
    flipped: Boolean,
    navigateToCallControlScreen: (action: String) -> Unit,
) {
    val pressOnceText = stringResource(R.string.press_once)
    val pressTwiceText = stringResource(R.string.press_twice)
    val muteUnmuteText = stringResource(R.string.mute_unmute)
    val hangUpText = stringResource(R.string.hang_up)

    StyledList(title = stringResource(R.string.call_controls)) {
        StyledListItem(
            name = stringResource(R.string.answer_call),
            description = pressOnceText,
            enabled = false,
            icon = Icons.Rounded.Call
        )
        StyledListItem(
            name = muteUnmuteText,
            description = if (flipped) pressTwiceText else pressOnceText,
            onClick = { navigateToCallControlScreen(muteUnmuteText) },
            icon = Icons.Rounded.MicOff
        )
        StyledListItem(
            name = hangUpText,
            description = if (flipped) pressOnceText else pressTwiceText,
            onClick = { navigateToCallControlScreen(hangUpText) },
            icon = Icons.Rounded.CallEnd
        )
    }
}
