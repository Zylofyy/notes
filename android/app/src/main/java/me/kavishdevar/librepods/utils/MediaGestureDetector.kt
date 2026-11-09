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

package me.kavishdevar.librepods.utils

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sign

/** Media actions that can be triggered with head movements. */
enum class MediaGesture { PLAY_PAUSE, NEXT, PREVIOUS }

/**
 * Detects deliberate media gestures in the AirPods head-tracking velocity stream
 * (the same horizontal/vertical channels the call gesture detector uses; they rest
 * near 0 and swing positive/negative while the head moves).
 *
 * The stream is cut into *bursts*: runs of motion bounded by stillness. Within a
 * burst each axis is split into *lobes* (consecutive samples above a threshold with
 * the same sign). A burst is then classified:
 *
 *  - **Double nod** (vertical, 4-5 alternating lobes): [MediaGesture.PLAY_PAUSE].
 *    A single nod (2-3 lobes) is ignored, because people nod along to music.
 *  - **Turn and back** (horizontal, exactly 2 lobes of opposite sign, the return at
 *    least [returnRatio] as strong as the turn): [MediaGesture.NEXT] or
 *    [MediaGesture.PREVIOUS], depending on the sign of the first lobe.
 *  - Shakes (3+ horizontal lobes), turning and holding (1 lobe), diagonal motion,
 *    weak motion, and long or busy bursts such as walking are ignored.
 *
 * After a gesture fires there is a [cooldownMs] during which nothing else fires.
 * Pure Kotlin with caller-supplied timestamps, so it can be unit-tested on the JVM.
 */
class MediaGestureDetector(
    private val lobeStart: Double = 600.0,
    private val lobeEnd: Double = 300.0,
    private val strongPeak: Double = 1000.0,
    private val returnRatio: Double = 0.4,
    private val dominanceRatio: Double = 1.6,
    private val quietMs: Long = 350,
    private val maxBurstMs: Long = 1800,
    private val maxLobes: Int = 8,
    private val cooldownMs: Long = 1200,
    /** Sign of the horizontal channel when the head turns right. */
    private val rightSign: Int = 1,
    private val onGesture: (MediaGesture) -> Unit,
) {
    private data class Lobe(val sign: Int, var peak: Double)

    private class Axis {
        val lobes = mutableListOf<Lobe>()
        var current: Lobe? = null

        fun maxPeak() = lobes.maxOfOrNull { it.peak } ?: 0.0

        /** Feeds one smoothed sample; returns true while this axis is "moving". */
        fun feed(value: Double, start: Double, end: Double): Boolean {
            val magnitude = abs(value)
            val s = value.sign.toInt()
            val lobe = current
            if (lobe != null) {
                if (magnitude >= end && s == lobe.sign) {
                    lobe.peak = max(lobe.peak, magnitude)
                    return true
                }
                current = null
            }
            if (magnitude >= start) {
                // Consecutive same-sign lobes split by a brief dip count as one.
                val last = lobes.lastOrNull()
                current = if (last != null && last.sign == s) last.also { it.peak = max(it.peak, magnitude) }
                else Lobe(s, magnitude).also { lobes += it }
                return true
            }
            return magnitude >= end
        }

        fun reset() {
            lobes.clear()
            current = null
        }
    }

    private val horizontal = Axis()
    private val vertical = Axis()
    private val hSmooth = ArrayDeque<Double>()
    private val vSmooth = ArrayDeque<Double>()

    private var burstStart = -1L
    private var lastMotion = 0L
    private var cooldownUntil = 0L

    /** Feeds one head-tracking sample taken at [timeMs]. */
    fun process(timeMs: Long, horizontalValue: Int, verticalValue: Int) {
        // Very large values are sensor calibration frames, not motion.
        if (abs(horizontalValue) > MAX_VALID || abs(verticalValue) > MAX_VALID) return

        val h = smooth(hSmooth, horizontalValue.toDouble())
        val v = smooth(vSmooth, verticalValue.toDouble())

        val moving = horizontal.feed(h, lobeStart, lobeEnd) or vertical.feed(v, lobeStart, lobeEnd)
        val anyLobe = horizontal.lobes.isNotEmpty() || vertical.lobes.isNotEmpty()

        if (moving) lastMotion = timeMs
        if (anyLobe && burstStart < 0) burstStart = timeMs

        if (burstStart >= 0 && !moving && timeMs - lastMotion >= quietMs) {
            finishBurst(timeMs)
        }
    }

    /** Clears all in-flight state, e.g. when tracking restarts. */
    fun reset() {
        horizontal.reset()
        vertical.reset()
        hSmooth.clear()
        vSmooth.clear()
        burstStart = -1L
    }

    private fun finishBurst(timeMs: Long) {
        val gesture = classify(lastMotion - burstStart)
        reset()
        if (gesture != null && timeMs >= cooldownUntil) {
            cooldownUntil = timeMs + cooldownMs
            onGesture(gesture)
        }
    }

    private fun classify(durationMs: Long): MediaGesture? {
        if (durationMs > maxBurstMs) return null
        if (horizontal.lobes.size + vertical.lobes.size > maxLobes) return null

        val hPeak = horizontal.maxPeak()
        val vPeak = vertical.maxPeak()
        if (max(hPeak, vPeak) < strongPeak) return null

        return when {
            vPeak >= dominanceRatio * hPeak -> classifyVertical()
            hPeak >= dominanceRatio * vPeak -> classifyHorizontal()
            else -> null
        }
    }

    private fun classifyVertical(): MediaGesture? {
        val lobes = vertical.lobes
        if (lobes.size !in 4..5) return null
        if (lobes.count { it.peak >= strongPeak } < 2) return null
        return MediaGesture.PLAY_PAUSE
    }

    private fun classifyHorizontal(): MediaGesture? {
        val lobes = horizontal.lobes
        if (lobes.size != 2) return null
        val (turn, back) = lobes
        if (turn.peak < strongPeak || back.peak < returnRatio * turn.peak) return null
        return if (turn.sign == rightSign) MediaGesture.NEXT else MediaGesture.PREVIOUS
    }

    private fun smooth(buffer: ArrayDeque<Double>, value: Double): Double {
        buffer.addLast(value)
        if (buffer.size > 3) buffer.removeFirst()
        return buffer.average()
    }

    private companion object {
        const val MAX_VALID = 6000
    }
}
