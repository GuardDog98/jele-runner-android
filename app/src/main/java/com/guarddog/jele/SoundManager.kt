package com.guarddog.jele

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.media.ToneGenerator
import android.media.AudioManager
import android.os.Build

/**
 * Small helper for playing short game sounds.
 *
 * Place a sound file at: app/src/main/res/raw/jele_sound.wav
 * (or name it coin/jump/gameover and update calls accordingly)
 */
class SoundManager(val context: Context) {
    private var soundPool: SoundPool? = null
    private var jeleSoundId: Int = 0
    private var tone: ToneGenerator? = null

    init {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val attrs = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
                soundPool = SoundPool.Builder().setMaxStreams(4).setAudioAttributes(attrs).build()
            } else {
                @Suppress("DEPRECATION")
                soundPool = SoundPool(4, AudioManager.STREAM_MUSIC, 0)
            }

            // try to load a bundled 'jele' sound if user added it to res/raw
            val resId = context.resources.getIdentifier("jele_sound", "raw", context.packageName)
            if (resId != 0) {
                jeleSoundId = soundPool?.load(context, resId, 1) ?: 0
            }
        } catch (e: Exception) {
            // fallback: initialize a ToneGenerator
            tone = ToneGenerator(AudioManager.STREAM_MUSIC, 80)
        }
    }

    fun playJele() {
        // primary: play the loaded wav
        if (jeleSoundId != 0 && soundPool != null) {
            soundPool?.play(jeleSoundId, 1f, 1f, 1, 0, 1f)
            return
        }

        // fallback: short tone
        try {
            if (tone == null) tone = ToneGenerator(AudioManager.STREAM_MUSIC, 80)
            tone?.startTone(ToneGenerator.TONE_PROP_BEEP, 140)
        } catch (_: Exception) { }
    }

    fun release() {
        soundPool?.release()
        soundPool = null
        tone?.release()
        tone = null
    }
}
