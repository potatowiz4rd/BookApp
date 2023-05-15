package com.example.bookapp

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.example.bookapp.databinding.ActivityAudioBookBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage


class AudioBookActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())
    var bookId = ""
    private lateinit var binding: ActivityAudioBookBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bookId = intent.getStringExtra("bookId")!!
        loadAudio()

        binding.rewindBtn.setOnClickListener {
            mediaPlayer?.seekTo(mediaPlayer?.currentPosition?.minus(5000) ?: 0)
        }

        binding.playBtn.setOnClickListener {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                binding.playBtn.setImageResource(R.drawable.play)
            } else {
                mediaPlayer?.start()
                binding.playBtn.setImageResource(R.drawable.pause)
            }
        }

        binding.forwardBtn.setOnClickListener {
            mediaPlayer?.seekTo(mediaPlayer?.currentPosition?.plus(5000) ?: 0)
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Do nothing
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Do nothing
            }
        })

    }

    private fun loadAudio() {

        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val audioUrl = snapshot.child("audio").value

                loadAudioFromUrl("$audioUrl")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun setDurationText(duration: Int) {
        binding.durationTv.text = formatTime(duration)
    }

    private fun formatTime(timeMs: Int): String {
        val minutes = timeMs / 1000 / 60
        val seconds = timeMs / 1000 % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun loadAudioFromUrl(audioUrl: String) {
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(audioUrl)
            prepareAsync()
            setOnPreparedListener { player ->
                player.start()
                binding.seekBar.max = mediaPlayer?.duration ?: 0
                setDurationText(mediaPlayer?.duration ?: 0)
                handler.postDelayed(object : Runnable {
                    override fun run() {
                        binding.seekBar.progress = mediaPlayer?.currentPosition ?: 0
                        binding.currentTv.text = formatTime(mediaPlayer?.currentPosition ?: 0)
                        handler.postDelayed(this, 1000)
                    }
                }, 1000)
            }
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                    binding.currentTv.text = formatTime(progress)
                } else {
                    binding.currentTv.text = formatTime(mediaPlayer?.currentPosition ?: 0)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Do nothing
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Do nothing
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }

}