package com.kolia.wbintercom

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

  private var mqtt: MqttManager? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val et = findViewById<EditText>(R.id.etRtsp)
    val btn = findViewById<Button>(R.id.btnPlay)

    et.setText("rtsp://admin:123456@192.168.1.18:554/stream1")

    btn.setOnClickListener {
      val rtsp = et.text.toString().trim()
      startActivity(Intent(this, PlayerActivity::class.java).putExtra("rtsp", rtsp))
    }
  }

  override fun onResume() {
    super.onResume()

    // Жёстко пересоздаём MQTT при каждом возврате в приложение
    mqtt?.stop()
    mqtt = MqttManager(this) {
      runOnUiThread {
        Toast.makeText(this, "DOORBELL!", Toast.LENGTH_LONG).show()
      }
    }
    mqtt?.start()
  }

  override fun onPause() {
    // Можно НЕ останавливать, но на Samsung безопаснее останавливать и поднимать заново
    mqtt?.stop()
    mqtt = null
    super.onPause()
  }
}
