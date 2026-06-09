package com.example.data

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.pow
import kotlin.math.sin

object AudioSynth {
    private val scope = CoroutineScope(Dispatchers.Default)

    fun playNote(midiNote: Int, durationMs: Long = 800) {
        val freq = midiNoteToFreq(midiNote)
        scope.launch {
            try {
                val sampleRate = 22050
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val samples = FloatArray(numSamples)
                
                // Construct a beautiful wave with decay envelope and harmonic tones for rich piano bell feel
                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    
                    // Natural exponential decay envelope
                    val progress = i.toDouble() / numSamples
                    val envelope = (1.0 - progress).pow(2.5)
                    
                    // Combine fundamental and first overtone
                    val fundamental = sin(2.0 * Math.PI * freq * t)
                    val overtone = 0.25 * sin(2.0 * Math.PI * (freq * 2.0) * t)
                    
                    samples[i] = ((fundamental + overtone) / 1.25 * envelope).toFloat()
                }

                // Convert to PCM short values
                val buffer = ShortArray(numSamples)
                for (i in 0 until numSamples) {
                    buffer[i] = (samples[i] * Short.MAX_VALUE).toInt().toShort()
                }

                val audioTrack = AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    buffer.size * 2,
                    AudioTrack.MODE_STATIC
                )

                audioTrack.write(buffer, 0, buffer.size)
                audioTrack.play()
                
                // Hold background thread and release resources
                Thread.sleep(durationMs + 100)
                audioTrack.stop()
                audioTrack.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun playChord(midiNotes: List<Int>, durationMs: Long = 1000) {
        midiNotes.forEach { midiNote ->
            // Let them start virtually together with slight delays
            playNote(midiNote, durationMs)
        }
    }

    fun midiNoteToFreq(midiNote: Int): Double {
        return 440.0 * 2.0.pow((midiNote - 69) / 12.0)
    }
}
