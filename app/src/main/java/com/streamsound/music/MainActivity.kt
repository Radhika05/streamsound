package com.streamsound.music

import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity

/**
 * Main activity class that handles media playback and user interactions.
 */
class MainActivity : AppCompatActivity() {
    // MediaPlayer instance for handling audio playback.
    private lateinit var mediaPlayer: MediaPlayer

    // Buttons for controlling playback and sharing the stream.
    private lateinit var playPauseButton: ImageButton
    private lateinit var shareButton: ImageButton
    private lateinit var volumeButton: ImageButton

    // ProgressBar to show loading and buffering status.
    private lateinit var progressBar: ProgressBar

    // Flag to track whether the audio is muted.
    private var isMuted: Boolean = false

    /**
     * Called when the activity is starting. This is where most initialization should go:
     * calling setContentView(int) to inflate the activity's UI, using findViewById(int)
     * to programmatically interact with widgets in the UI.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI components and setup media player.
        initializeUI()
        setupMediaPlayer()
    }

    /**
     * Initializes UI components and sets up listeners for buttons.
     */
    private fun initializeUI() {
        // Retrieve references to UI components.
        playPauseButton = findViewById(R.id.playPauseButton)
        progressBar = findViewById(R.id.progressBar)
        shareButton = findViewById(R.id.shareButton)
        volumeButton = findViewById(R.id.volumeButton)

        // Set up click listeners for the play/pause, share, and volume buttons.
        playPauseButton.setOnClickListener { togglePlayPause() }
        shareButton.setOnClickListener { shareStreamUrl() }
        volumeButton.setOnClickListener { toggleMute() }
    }

    /**
     * Toggles the playback state of the MediaPlayer.
     */
    private fun togglePlayPause() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            playPauseButton.setImageResource(android.R.drawable.ic_media_play)
        } else {
            mediaPlayer.start()
            playPauseButton.setImageResource(android.R.drawable.ic_media_pause)
        }
    }

    /**
     * Shares the current stream URL using an intent.
     */
    private fun shareStreamUrl() {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, getString(R.string.txt_listening))
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, "Share link using"))
    }

    /**
     * Toggles the mute state of the MediaPlayer.
     */
    private fun toggleMute() {
        isMuted = !isMuted
        mediaPlayer.setVolume(if (isMuted) 0f else 1f, if (isMuted) 0f else 1f)
        volumeButton.setImageResource(
            if (isMuted) android.R.drawable.ic_lock_silent_mode_off
            else android.R.drawable.ic_lock_silent_mode
        )
    }

    /**
     * Sets up the MediaPlayer, including its source and audio attributes.
     */
    private fun setupMediaPlayer() {
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource("https://s2.radio.co/sd32e5875e/listen")
            prepareAsync()
            setOnPreparedListener {
                progressBar.visibility = ProgressBar.GONE
                playPauseButton.setImageResource(if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play)
            }
            setOnBufferingUpdateListener { _, percent ->
                progressBar.visibility = if (percent < 100) ProgressBar.VISIBLE else ProgressBar.GONE
            }
        }
    }

    /**
     * Called by the system before the activity is destroyed.
     * This callback is the final one that the activity receives.
     */
    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer.release()
    }
}
