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

package me.kavishdevar.librepods.services

import android.content.Context
import android.media.AudioManager
import android.media.AudioPlaybackConfiguration
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import me.kavishdevar.librepods.utils.GestureFeedback
import me.kavishdevar.librepods.utils.MediaController
import me.kavishdevar.librepods.utils.MediaGesture
import me.kavishdevar.librepods.utils.MediaGestureDetector

/**
 * Controls media playback with head gestures (double nod: play/pause; turn right and
 * back: next; turn left and back: previous).
 *
 * Head tracking costs AirPods battery and could misfire if it ran all the time, so a
 * session only runs while music is playing on this phone, plus [GRACE_MS] after it
 * pauses so a nod can resume it. Detection is suspended during phone calls, where the
 * nod/shake call gestures take over.
 */
class MediaHeadGestures(
    private val context: Context,
    private val audioManager: AudioManager,
    /** AirPods linked and owned by this phone, so tracking won't steal them from another device. */
    private val canUseHeadTracking: () -> Boolean,
    private val acquireHeadTracking: () -> Unit,
    private val releaseHeadTracking: () -> Unit,
) {
    private val handler = Handler(Looper.getMainLooper())
    private val lock = Any()

    private var enabled = false
    private var swapDirections = false
    @Volatile private var suspended = false
    @Volatile private var sessionActive = false
    private var detector = newDetector()
    private val feedback by lazy { GestureFeedback(context) }

    private val endSessionRunnable = Runnable { endSession() }
    private val evaluateRunnable = Runnable { evaluate() }

    private val playbackCallback = object : AudioManager.AudioPlaybackCallback() {
        override fun onPlaybackConfigChanged(configs: MutableList<AudioPlaybackConfiguration>?) {
            evaluate()
        }
    }

    fun start() {
        audioManager.registerAudioPlaybackCallback(playbackCallback, handler)
        evaluate()
    }

    fun destroy() {
        audioManager.unregisterAudioPlaybackCallback(playbackCallback)
        handler.removeCallbacks(endSessionRunnable)
        handler.removeCallbacks(evaluateRunnable)
        endSession()
    }

    /** Applies the user's settings; [enabled] already includes the master head-gesture switch. */
    fun configure(enabled: Boolean, swapDirections: Boolean) {
        handler.post {
            val changedDirections = swapDirections != this.swapDirections
            this.enabled = enabled
            this.swapDirections = swapDirections
            if (changedDirections) synchronized(lock) { detector = newDetector() }
            evaluate()
        }
    }

    /** Pauses detection while a call is ringing or active. */
    fun setSuspended(suspended: Boolean) {
        handler.post {
            this.suspended = suspended
            synchronized(lock) { detector.reset() }
            if (!suspended) evaluate()
        }
    }

    /** The AirPods went away; end the session so the next connection starts clean. */
    fun onAirPodsDisconnected() {
        handler.post {
            handler.removeCallbacks(endSessionRunnable)
            endSession()
        }
    }

    /** Called from the head-tracking packet thread for every sensor sample. */
    fun onSample(horizontal: Int, vertical: Int) {
        if (!sessionActive || suspended) return
        synchronized(lock) { detector.process(SystemClock.uptimeMillis(), horizontal, vertical) }
    }

    private fun evaluate() {
        handler.removeCallbacks(evaluateRunnable)
        val playing = audioManager.isMusicActive
        val available = canUseHeadTracking()
        when {
            enabled && !suspended && playing && available -> {
                handler.removeCallbacks(endSessionRunnable)
                if (!sessionActive) beginSession()
            }
            enabled && !suspended && playing && !sessionActive -> {
                // Music just started; the phone usually takes the AirPods over a moment later.
                handler.postDelayed(evaluateRunnable, OWNERSHIP_RETRY_MS)
            }
            !sessionActive -> Unit
            !enabled || !available -> endSession()
            else -> {
                // Paused (or a call started): keep listening a little longer so a nod can resume.
                handler.removeCallbacks(endSessionRunnable)
                handler.postDelayed(endSessionRunnable, GRACE_MS)
            }
        }
    }

    private fun beginSession() {
        sessionActive = true
        synchronized(lock) { detector.reset() }
        Log.d(TAG, "Session started")
        acquireHeadTracking()
    }

    private fun endSession() {
        if (!sessionActive) return
        sessionActive = false
        Log.d(TAG, "Session ended")
        releaseHeadTracking()
    }

    private fun newDetector() = MediaGestureDetector(
        rightSign = if (swapDirections) -1 else 1,
        onGesture = { gesture -> handler.post { perform(gesture) } }
    )

    private fun perform(gesture: MediaGesture) {
        if (!sessionActive || suspended) return
        Log.d(TAG, "Media gesture: $gesture")
        when (gesture) {
            MediaGesture.PLAY_PAUSE -> MediaController.sendPlayPause()
            MediaGesture.NEXT -> MediaController.sendNextTrack()
            MediaGesture.PREVIOUS -> MediaController.sendPreviousTrack()
        }
        feedback.playConfirmation(true)
        // A pause starts the grace period; a play cancels it. The playback callback
        // usually covers this, but MediaController briefly ignores self-caused changes.
        handler.postDelayed({ evaluate() }, 500)
    }

    companion object {
        private const val TAG = "MediaHeadGestures"
        const val GRACE_MS = 30_000L
        private const val OWNERSHIP_RETRY_MS = 2_000L
        const val PREF_ENABLED = "media_head_gestures"
        const val PREF_SWAP = "media_head_gestures_swap"
    }
}
