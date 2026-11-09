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
import androidx.compose.material.icons.rounded.Hearing
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import me.kavishdevar.librepods.R

@Composable
fun HearingHealthSettings(
    hasPPECapability: Boolean,
    hasHearingAidCapability: Boolean,
    vendorIdHook: Boolean,
    navigateToHearingProtection: () -> Unit,
    navigateToHearingAid: () -> Unit
) {
    val showHearingAid = hasHearingAidCapability && vendorIdHook
    if (!hasPPECapability && !showHearingAid) return

    StyledList(title = stringResource(R.string.hearing_health)) {
        if (hasPPECapability) {
            StyledListItem(
                name = stringResource(R.string.hearing_protection),
                onClick = navigateToHearingProtection,
                icon = Icons.Rounded.Shield
            )
        }
        if (showHearingAid) {
            StyledListItem(
                name = stringResource(R.string.hearing_aid),
                onClick = navigateToHearingAid,
                icon = Icons.Rounded.Hearing
            )
        }
    }
}
