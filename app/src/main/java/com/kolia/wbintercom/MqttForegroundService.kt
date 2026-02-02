package com.kolia.wbintercom

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat

class MqttForegroundService : Service() {

  private var mqtt: MqttManager? = null

  override fun onCreate() {
    super.onCreate()

    // Стартуем foreground-сервис с постоянным уведомлением
    startForeground(1001, buildNotification("WB Intercom: waiting for doorbell"))

    // Поднимаем MQTT и реагируем на doorbell:
    // 1) звук + вибра
    // 2) открываем экран звонка поверх блокировки
    mqtt = MqttManager(this) {
      playDoorbellSoundAndVibrate()

      val i = Intent(this, IncomingCallActivity::class.java).apply {
        addFlags(
          Intent.FLAG_ACTIVITY_NEW_TASK or
            Intent.FLAG_ACTIVITY_SINGLE_TOP or
            Intent.FLAG_ACTIVITY_CLEAR_TOP
        )
      }
      startActivity(i)
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

  private fun playDoorbellSoundAndVibrate() {
    // Звук
    try {
      val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
      val r = RingtoneManager.getRingtone(this, uri)
      r?.play()
    } catch (_: Exception) {
      // игнорируем
    }

    // Вибра
    try {
      val vib = if (Build.VERSION.SDK_INT >= 31) {
        val vm = getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vm.defaultVibrator
      } else {
        @Suppress("DEPRECATION")
        getSystemService(VIBRATOR_SERVICE) as Vibrator
      }

      if (Build.VERSION.SDK_INT >= 26) {
        vib.vibrate(VibrationEffect.createOneShot(250, VibrationEffect.DEFAULT_AMPLITUDE))
      } else {
        @Suppress("DEPRECATION")
        vib.vibrate(250)
      }
    } catch (_: Exception) {
      // игнорируем
    }
  }

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
