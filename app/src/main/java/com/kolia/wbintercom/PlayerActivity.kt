package com.kolia.wbintercom

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.rtsp.RtspMediaSource
import androidx.media3.ui.PlayerView

class PlayerActivity : AppCompatActivity() {
  private var player: ExoPlayer? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_player)

    val rtsp = intent.getStringExtra("rtsp") ?: ""
    if (rtsp.isBlank()) {
      finish(); return
    }

    val pv = findViewById<PlayerView>(R.id.playerView)

    player = ExoPlayer.Builder(this).build().also { p ->
      pv.player = p
      val item = MediaItem.fromUri(Uri.parse(rtsp))
      val src = RtspMediaSource.Factory().createMediaSource(item)
      p.setMediaSource(src)
      p.prepare()
      p.play()
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    player?.release()
    player = null
  }
}
