package com.example.flowstate.util

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import java.io.File

data class MusicTrack(
    val resourceId: Int = 0,
    val filePath: String? = null,
    val name: String
) {
    val isFromFile: Boolean get() = filePath != null
}

class MusicPlayer(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private val _isPlaying = mutableStateOf(false)
    val isPlaying: State<Boolean> = _isPlaying

    private val _currentTrack = mutableStateOf<MusicTrack?>(null)
    val currentTrack: State<MusicTrack?> = _currentTrack

    fun play(track: MusicTrack) {
        stop()
        try {
            mediaPlayer = if (track.isFromFile) {
                MediaPlayer().apply {
                    setDataSource(track.filePath!!)
                    prepare()
                }
            } else {
                MediaPlayer.create(context, track.resourceId)
            }

            mediaPlayer?.apply {
                isLooping = true
                setVolume(0.5f, 0.5f)
                start()
                _isPlaying.value = true
                _currentTrack.value = track
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _isPlaying.value = false
            _currentTrack.value = null
        }
    }

    fun pause() {
        mediaPlayer?.pause()
        _isPlaying.value = false
    }

    fun resume() {
        mediaPlayer?.start()
        _isPlaying.value = true
    }

    fun stop() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null
        _isPlaying.value = false
        _currentTrack.value = null
    }

    fun setVolume(volume: Float) {
        mediaPlayer?.setVolume(volume, volume)
    }

    fun release() {
        stop()
    }

    companion object {
        fun getMusicTracks(context: Context): List<MusicTrack> {
            val tracks = mutableListOf<MusicTrack>()

            // Load from res/raw
            try {
                val fields = Class.forName("${context.packageName}.R\$raw").declaredFields
                for (field in fields) {
                    try {
                        val resourceId = field.getInt(null)
                        val fileName = field.name
                        if (fileName != "music_readme") {
                            val displayName = fileName
                                .replace('_', ' ')
                                .split(' ')
                                .joinToString(" ") { word ->
                                    word.replaceFirstChar { it.uppercase() }
                                }
                            tracks.add(MusicTrack(resourceId = resourceId, name = displayName))
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // Load from app's music directory
            try {
                val musicDir = File(context.filesDir, "music")
                if (musicDir.exists() && musicDir.isDirectory) {
                    musicDir.listFiles { file ->
                        file.extension.lowercase() in listOf("mp3", "m4a", "wav", "ogg")
                    }?.forEach { file ->
                        val displayName = file.nameWithoutExtension
                            .replace('_', ' ')
                            .split(' ')
                            .joinToString(" ") { word ->
                                word.replaceFirstChar { it.uppercase() }
                            }
                        tracks.add(MusicTrack(filePath = file.absolutePath, name = displayName))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return tracks.sortedBy { it.name }
        }
    }
}
