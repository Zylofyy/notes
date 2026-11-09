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

package me.kavishdevar.librepods.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import me.kavishdevar.librepods.R

/*
 * Material 3 Expressive type scale.
 *
 * Sizes follow the M3 baseline scale. Roboto Flex is loaded through downloadable
 * fonts and every role gets two variable-font instances: a regular one, and an
 * "emphasized" one with more weight and width for the *Emphasized styles M3E uses
 * for headlines, selected states and key actions.
 */

private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

private fun robotoFlex(
    weight: Int,
    width: Float = 100f,
    grade: Float = 0f,
) = FontFamily(
    Font(
        googleFont = GoogleFont("Roboto Flex"),
        fontProvider = provider,
        weight = FontWeight(weight),
        variationSettings = FontVariation.Settings(
            FontVariation.weight(weight),
            FontVariation.width(width),
            FontVariation.grade(grade.toInt()),
        )
    )
)

private val displayFamily = robotoFlex(weight = 475, width = 110f)
private val displayEmphasizedFamily = robotoFlex(weight = 720, width = 125f, grade = 50f)
private val headlineFamily = robotoFlex(weight = 500, width = 105f)
private val headlineEmphasizedFamily = robotoFlex(weight = 700, width = 120f, grade = 25f)
private val bodyFamily = robotoFlex(weight = 400)
private val bodyEmphasizedFamily = robotoFlex(weight = 560, width = 105f)
private val labelFamily = robotoFlex(weight = 500)
private val labelEmphasizedFamily = robotoFlex(weight = 680, width = 110f)

private fun TextStyle.with(family: FontFamily) = copy(fontFamily = family, fontWeight = null)

val LibrePodsTypography: Typography = Typography().run {
    copy(
        displayLarge = displayLarge.with(displayFamily),
        displayMedium = displayMedium.with(displayFamily),
        displaySmall = displaySmall.with(displayFamily),
        headlineLarge = headlineLarge.with(headlineFamily),
        headlineMedium = headlineMedium.with(headlineFamily),
        headlineSmall = headlineSmall.with(headlineFamily),
        titleLarge = titleLarge.with(headlineFamily),
        titleMedium = titleMedium.with(labelFamily),
        titleSmall = titleSmall.with(labelFamily),
        bodyLarge = bodyLarge.with(bodyFamily),
        bodyMedium = bodyMedium.with(bodyFamily),
        bodySmall = bodySmall.with(bodyFamily),
        labelLarge = labelLarge.with(labelFamily),
        labelMedium = labelMedium.with(labelFamily),
        labelSmall = labelSmall.with(labelFamily),

        displayLargeEmphasized = displayLargeEmphasized.with(displayEmphasizedFamily),
        displayMediumEmphasized = displayMediumEmphasized.with(displayEmphasizedFamily),
        displaySmallEmphasized = displaySmallEmphasized.with(displayEmphasizedFamily),
        headlineLargeEmphasized = headlineLargeEmphasized.with(headlineEmphasizedFamily),
        headlineMediumEmphasized = headlineMediumEmphasized.with(headlineEmphasizedFamily),
        headlineSmallEmphasized = headlineSmallEmphasized.with(headlineEmphasizedFamily),
        titleLargeEmphasized = titleLargeEmphasized.with(headlineEmphasizedFamily),
        titleMediumEmphasized = titleMediumEmphasized.with(labelEmphasizedFamily),
        titleSmallEmphasized = titleSmallEmphasized.with(labelEmphasizedFamily),
        bodyLargeEmphasized = bodyLargeEmphasized.with(bodyEmphasizedFamily),
        bodyMediumEmphasized = bodyMediumEmphasized.with(bodyEmphasizedFamily),
        bodySmallEmphasized = bodySmallEmphasized.with(bodyEmphasizedFamily),
        labelLargeEmphasized = labelLargeEmphasized.with(labelEmphasizedFamily),
        labelMediumEmphasized = labelMediumEmphasized.with(labelEmphasizedFamily),
        labelSmallEmphasized = labelSmallEmphasized.with(labelEmphasizedFamily),
    )
}
