package com.kolia.wbintercom

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class MqttForegroundService : Service() {

  private var mqtt: MqttManager? = null

  override fun onCreate() {
    super.onCreate()

    // Стартуем foreground-сервис с постоянным уведомлением
    startForeground(1001, buildNotification("WB Intercom: waiting for doorbell"))

    // Поднимаем MQTT и реагируем на doorbell (пока через обновление уведомления)
    mqtt = MqttManager(this) {
      val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
      nm.notify(1001, buildNotification("WB Intercom: DOORBELL!"))
    }
    mqtt?.start()
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    // Держим сервис живым (Android может перезапустить его)
    return START_STICKY
  }

  override fun onDestroy() {
    mqtt?.stop()
    mqtt = null
    super.onDestroy()
  }

  override fun onBind(intent: Intent?): IBinder? = null

  private fun buildNotification(text: String): Notification {
    val channelId = "wb_intercom_service"

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val ch = NotificationChannel(
        channelId,
        "WB Intercom",
        NotificationManager.IMPORTANCE_LOW
      )
      val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
      nm.createNotificationChannel(ch)
    }

    return NotificationCompat.Builder(this, channelId)
      .setSmallIcon(android.R.drawable.stat_notify_more)
      .setContentTitle("WB Intercom")
      .setContentText(text)
      .setOngoing(true)
      .build()
  }
}
