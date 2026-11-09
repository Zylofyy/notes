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
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.AutoMode
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.NoiseAware
import androidx.compose.material.icons.rounded.RecordVoiceOver
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import me.kavishdevar.librepods.R

@Composable
fun AudioSettings(
    adaptiveVolumeCapability: Boolean,
    conversationAwarenessCapability: Boolean,
    loudSoundReductionCapability: Boolean,
    adaptiveAudioCapability: Boolean,
    customEqCapability: Boolean,
    adaptiveVolumeChecked: Boolean,
    onAdaptiveVolumeCheckedChange: (Boolean) -> Unit,
    conversationAwarenessChecked: Boolean,
    onConversationAwarenessCheckedChange: (Boolean) -> Unit,
    loudSoundReductionChecked: Boolean,
    onLoudSoundReductionCheckedChange: (Boolean) -> Unit,
    navigateToAdaptiveStrength: () -> Unit,
    navigateToEqualizer: () -> Unit,
    vendorIdHook: Boolean,
    isPremium: Boolean
) {
    if (!(adaptiveVolumeCapability || conversationAwarenessCapability || loudSoundReductionCapability || adaptiveAudioCapability)) return

    StyledList(title = stringResource(R.string.audio)) {
        if (adaptiveVolumeCapability) {
            StyledToggle(
                label = stringResource(R.string.personalized_volume),
                description = stringResource(R.string.personalized_volume_description),
                checked = adaptiveVolumeChecked,
                onCheckedChange = onAdaptiveVolumeCheckedChange,
                enabled = isPremium,
                icon = Icons.AutoMirrored.Rounded.VolumeUp
            )
        }
        if (conversationAwarenessCapability) {
            StyledToggle(
                label = stringResource(R.string.conversation_awareness),
                description = stringResource(R.string.conversation_awareness_description),
                checked = conversationAwarenessChecked,
                onCheckedChange = onConversationAwarenessCheckedChange,
                enabled = isPremium,
                icon = Icons.Rounded.RecordVoiceOver
            )
        }
        if (loudSoundReductionCapability && vendorIdHook) {
            StyledToggle(
                label = stringResource(R.string.loud_sound_reduction),
                description = stringResource(R.string.loud_sound_reduction_description),
                checked = loudSoundReductionChecked,
                onCheckedChange = onLoudSoundReductionCheckedChange,
                enabled = isPremium,
                icon = Icons.Rounded.NoiseAware
            )
        }
        if (adaptiveAudioCapability) {
            StyledListItem(
                name = stringResource(R.string.adaptive_audio),
                onClick = navigateToAdaptiveStrength,
                icon = Icons.Rounded.AutoMode
            )
        }
        if (customEqCapability) {
            StyledListItem(
                name = stringResource(R.string.equalizer),
                onClick = navigateToEqualizer,
                icon = Icons.Rounded.GraphicEq
            )
        }
    }
}
