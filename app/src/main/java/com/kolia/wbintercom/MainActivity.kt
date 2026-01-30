package com.kolia.wbintercom

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    // Android 13+ просит разрешение на уведомления (иначе не видно FG-уведомление)
    if (Build.VERSION.SDK_INT >= 33) {
      val ok = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
        PackageManager.PERMISSION_GRANTED
      if (!ok) {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 100)
      }
    }

    // Запускаем foreground service, который держит MQTT в фоне
    startForegroundService(Intent(this, MqttForegroundService::class.java))

    val et = findViewById<EditText>(R.id.etRtsp)
    val btn = findViewById<Button>(R.id.btnPlay)

    et.setText("rtsp://admin:123456@192.168.1.18:554/stream1")

    btn.setOnClickListener {
      val rtsp = et.text.toString().trim()
      startActivity(Intent(this, PlayerActivity::class.java).putExtra("rtsp", rtsp))
    }
  }
}
