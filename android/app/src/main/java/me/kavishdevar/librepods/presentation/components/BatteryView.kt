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
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.WavyProgressIndicatorDefaults
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.RoundedPolygon
import me.kavishdevar.librepods.R
import me.kavishdevar.librepods.data.Battery
import me.kavishdevar.librepods.data.BatteryComponent
import me.kavishdevar.librepods.data.BatteryStatus

/**
 * Home-screen hero: the buds and case art sit on tonal Material shapes, with a
 * wavy circular progress ring per component underneath. Rings ripple while that
 * component is charging and turn to the error color at 20% or below.
 */
@Composable
fun BatteryView(
    batteryList: List<Battery>,
    budsRes: Int,
    caseRes: Int,
    modifier: Modifier = Modifier,
) {
    val left = batteryList.find { it.component == BatteryComponent.LEFT }
    val right = batteryList.find { it.component == BatteryComponent.RIGHT }
    val case = batteryList.find { it.component == BatteryComponent.CASE }

    val leftLevel = left?.level ?: 0
    val rightLevel = right?.level ?: 0
    // Show one "Buds" ring when both buds agree (same status, within 3%).
    val combineBuds = left?.status == right?.status && (leftLevel - rightLevel) in -3..3

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = listContainerColor,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 420.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DeviceArt(
                    bitmap = ImageBitmap.imageResource(budsRes),
                    contentDescription = stringResource(R.string.buds),
                    shape = MaterialShapes.Cookie9Sided,
                    modifier = Modifier.weight(1f)
                )
                DeviceArt(
                    bitmap = ImageBitmap.imageResource(caseRes),
                    contentDescription = stringResource(R.string.case_alt),
                    shape = MaterialShapes.Cookie7Sided,
                    imageScale = 0.72f,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                if (combineBuds) {
                    if (left.isPresent() || right.isPresent()) {
                        BatteryRing(
                            label = stringResource(R.string.buds),
                            level = minOf(leftLevel, rightLevel),
                            status = left?.status ?: BatteryStatus.NOT_CHARGING
                        )
                    }
                } else {
                    if (left.isPresent()) {
                        BatteryRing(stringResource(R.string.left), leftLevel, left!!.status)
                    }
                    if (right.isPresent()) {
                        BatteryRing(stringResource(R.string.right), rightLevel, right!!.status)
                    }
                }
                if (case.isPresent()) {
                    BatteryRing(stringResource(R.string.case_alt), case!!.level, case.status)
                }
            }
        }
    }
}

private fun Battery?.isPresent() = this != null && (level > 0 || status != BatteryStatus.DISCONNECTED)

@Composable
private fun DeviceArt(
    bitmap: ImageBitmap,
    contentDescription: String,
    shape: RoundedPolygon,
    modifier: Modifier = Modifier,
    imageScale: Float = 0.9f,
) {
    Box(
        modifier = modifier.aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(0.94f)
                .clip(shape.toShape())
                .background(MaterialTheme.colorScheme.secondaryContainer)
        )
        Image(
            bitmap = bitmap,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(imageScale)
        )
    }
}

@Composable
fun BatteryRing(
    label: String,
    level: Int,
    status: Int,
    modifier: Modifier = Modifier,
) {
    val charging = status == BatteryStatus.CHARGING || status == BatteryStatus.OPTIMIZED_CHARGING
    val low = level <= 20 && !charging
    val progress by animateFloatAsState((level / 100f).coerceIn(0f, 1f), label = "batteryProgress")
    val amplitude by animateFloatAsState(if (charging) 1f else 0f, label = "batteryAmplitude")
    val ringColor by animateColorAsState(
        when {
            low -> MaterialTheme.colorScheme.error
            charging -> MaterialTheme.colorScheme.tertiary
            else -> MaterialTheme.colorScheme.primary
        },
        label = "batteryColor"
    )
    val strokeWidth = with(LocalDensity.current) { 6.dp.toPx() }
    val stroke = Stroke(width = strokeWidth, cap = StrokeCap.Round)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            CircularWavyProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(76.dp),
                color = ringColor,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                stroke = stroke,
                trackStroke = stroke,
                amplitude = { amplitude },
                wavelength = WavyProgressIndicatorDefaults.CircularWavelength,
                waveSpeed = if (charging) WavyProgressIndicatorDefaults.CircularWavelength else 0.dp,
            )
            Text(
                text = stringResource(R.string.percent_value, level),
                style = MaterialTheme.typography.titleMediumEmphasized,
                color = MaterialTheme.colorScheme.onSurface
            )
            val badgeScale by animateFloatAsState(if (charging) 1f else 0f, label = "chargingBadge")
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = (-4).dp)
                    .graphicsLayer {
                        scaleX = badgeScale
                        scaleY = badgeScale
                        alpha = badgeScale.coerceIn(0f, 1f)
                    }
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.tertiaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Rounded.Bolt,
                    contentDescription = if (charging) stringResource(R.string.charging) else null,
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (status == BatteryStatus.OPTIMIZED_CHARGING) {
            Text(
                text = stringResource(R.string.charge_limited),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}
