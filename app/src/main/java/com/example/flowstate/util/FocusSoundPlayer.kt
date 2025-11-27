package com.example.flowstate.util

import android.content.Context
import android.media.MediaPlayer
import com.example.flowstate.R

class FocusSoundPlayer(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private var currentSound: FocusSound? = null

    enum class FocusSound {
        RAIN, CAFE
    }

    fun play(sound: FocusSound) {
        // If the same sound is already playing, do nothing
        if (currentSound == sound && mediaPlayer?.isPlaying == true) {
            return
        }

        stop()

        try {
            val resourceId = when (sound) {
                FocusSound.RAIN -> R.raw.rain
                FocusSound.CAFE -> R.raw.cafe
            }

            mediaPlayer = MediaPlayer.create(context, resourceId)?.apply {
                isLooping = true
                setVolume(0.3f, 0.3f) // Quieter volume for background sounds
                start()
            }
            currentSound = sound
        } catch (e: Exception) {
            e.printStackTrace()
            currentSound = null
        }
    }

    fun stop() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null
        currentSound = null
    }

    fun release() {
        stop()
    }

    fun isPlaying(): Boolean = mediaPlayer?.isPlaying == true
}
