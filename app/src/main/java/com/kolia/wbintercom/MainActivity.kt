package com.kolia.wbintercom

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val et = findViewById<EditText>(R.id.etRtsp)
    val btn = findViewById<Button>(R.id.btnPlay)

    // можно сразу подставить твой поток, чтобы не вводить каждый раз:
    // et.setText("rtsp://admin:123456@192.168.0.213:554/stream1")

    btn.setOnClickListener {
      val rtsp = et.text.toString().trim()
      startActivity(Intent(this, PlayerActivity::class.java).putExtra("rtsp", rtsp))
    }
  }
}
