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

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.PI
import kotlin.math.sin

/**
 * Feeds synthetic head-tracking velocity traces (rest = 0, a movement is a signed
 * lobe; compare the "Velocity" plot on the Head Tracking screen) into the detector.
 */
class MediaGestureDetectorTest {

    private class Rig(stepMs: Long = 20, rightSign: Int = 1) {
        val gestures = mutableListOf<MediaGesture>()
        val detector = MediaGestureDetector(rightSign = rightSign) { gestures += it }
        private val step = stepMs
        var now = 0L

        /** Plays [durationMs] of signal; h/v receive the time since the segment started. */
        fun play(durationMs: Long, h: (Long) -> Double = { 0.0 }, v: (Long) -> Double = { 0.0 }) {
            var t = 0L
            while (t < durationMs) {
                detector.process(now, h(t).toInt(), v(t).toInt())
                now += step
                t += step
            }
        }

        fun rest(durationMs: Long = 600) = play(durationMs)
    }

    /** Half-sine lobe of [amplitude] lasting [lengthMs]. */
    private fun lobe(t: Long, lengthMs: Long, amplitude: Double) =
        if (t in 0 until lengthMs) amplitude * sin(PI * t / lengthMs) else 0.0

    /** Turn one way and come back: an outward lobe, then a weaker opposite lobe. */
    private fun turnAndBack(direction: Int, amplitude: Double = 1600.0, lobeMs: Long = 260): (Long) -> Double = { t ->
        lobe(t, lobeMs, direction * amplitude) + lobe(t - lobeMs, lobeMs, -direction * amplitude * 0.8)
    }

    /** [cycles] full oscillations (two lobes each). */
    private fun oscillation(cycles: Double, periodMs: Long, amplitude: Double): (Long) -> Double = { t ->
        if (t < cycles * periodMs) amplitude * sin(2 * PI * t / periodMs) else 0.0
    }

    private fun crossTalk(source: (Long) -> Double): (Long) -> Double = { t -> source(t) * 0.2 }

    @Test
    fun turnRightAndBackIsNext() {
        val rig = Rig()
        rig.rest()
        val turn = turnAndBack(+1)
        rig.play(600, h = turn, v = crossTalk(turn))
        rig.rest()
        assertEquals(listOf(MediaGesture.NEXT), rig.gestures)
    }

    @Test
    fun turnLeftAndBackIsPrevious() {
        val rig = Rig()
        rig.rest()
        rig.play(600, h = turnAndBack(-1))
        rig.rest()
        assertEquals(listOf(MediaGesture.PREVIOUS), rig.gestures)
    }

    @Test
    fun swapSettingInvertsDirection() {
        val rig = Rig(rightSign = -1)
        rig.rest()
        rig.play(600, h = turnAndBack(+1))
        rig.rest()
        assertEquals(listOf(MediaGesture.PREVIOUS), rig.gestures)
    }

    @Test
    fun doubleNodIsPlayPause() {
        val rig = Rig()
        rig.rest()
        val nods = oscillation(cycles = 2.0, periodMs = 420, amplitude = 1500.0)
        rig.play(1000, v = nods, h = crossTalk(nods))
        rig.rest()
        assertEquals(listOf(MediaGesture.PLAY_PAUSE), rig.gestures)
    }

    @Test
    fun worksAtLowerSampleRate() {
        val rig = Rig(stepMs = 40)
        rig.rest()
        rig.play(600, h = turnAndBack(+1))
        rig.rest()
        rig.rest(1400) // past the cooldown
        rig.play(1000, v = oscillation(2.0, 420, 1500.0))
        rig.rest()
        assertEquals(listOf(MediaGesture.NEXT, MediaGesture.PLAY_PAUSE), rig.gestures)
    }

    @Test
    fun singleNodIsIgnored() {
        val rig = Rig()
        rig.rest()
        rig.play(500, v = oscillation(cycles = 1.0, periodMs = 420, amplitude = 1500.0))
        rig.rest()
        assertEquals(emptyList<MediaGesture>(), rig.gestures)
    }

    @Test
    fun headShakeIsIgnored() {
        val rig = Rig()
        rig.rest()
        rig.play(1500, h = oscillation(cycles = 3.5, periodMs = 380, amplitude = 1800.0))
        rig.rest()
        assertEquals(emptyList<MediaGesture>(), rig.gestures)
    }

    @Test
    fun turningAndHoldingIsIgnored() {
        val rig = Rig()
        rig.rest()
        rig.play(400, h = { t -> lobe(t, 300, 1600.0) })
        rig.rest(2000)
        assertEquals(emptyList<MediaGesture>(), rig.gestures)
    }

    @Test
    fun gentleMovementIsIgnored() {
        val rig = Rig()
        rig.rest()
        rig.play(600, h = turnAndBack(+1, amplitude = 800.0))
        rig.rest()
        rig.play(1000, v = oscillation(2.0, 420, 800.0))
        rig.rest()
        assertEquals(emptyList<MediaGesture>(), rig.gestures)
    }

    @Test
    fun diagonalMovementIsIgnored() {
        val rig = Rig()
        rig.rest()
        val turn = turnAndBack(+1)
        rig.play(600, h = turn, v = turn)
        rig.rest()
        assertEquals(emptyList<MediaGesture>(), rig.gestures)
    }

    @Test
    fun walkingBobIsIgnored() {
        val rig = Rig()
        rig.rest()
        // Continuous vertical bobbing with a little sway, never pausing.
        rig.play(10_000, v = oscillation(20.0, 500, 1300.0), h = oscillation(10.0, 1000, 400.0))
        rig.rest()
        assertEquals(emptyList<MediaGesture>(), rig.gestures)
    }

    @Test
    fun secondGestureInsideCooldownIsDropped() {
        val rig = Rig()
        rig.rest()
        rig.play(600, h = turnAndBack(+1))
        rig.rest(400) // burst ends; still inside the 1.2 s cooldown
        rig.play(600, h = turnAndBack(+1))
        rig.rest()
        rig.rest(1200)
        rig.play(600, h = turnAndBack(-1))
        rig.rest()
        assertEquals(listOf(MediaGesture.NEXT, MediaGesture.PREVIOUS), rig.gestures)
    }

    @Test
    fun calibrationFramesAreIgnored() {
        val rig = Rig()
        rig.rest()
        rig.play(400, h = { 19_000.0 }, v = { -12_000.0 })
        rig.rest()
        assertEquals(emptyList<MediaGesture>(), rig.gestures)
    }
}
