package com.kolia.wbintercom

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage

class FcmService : FirebaseMessagingService() {

  override fun onNewToken(token: String) {
    // Подписываем устройство на общий топик семьи
    FirebaseMessaging.getInstance().subscribeToTopic("wbintercom-family")

    // На первом этапе показываем токен уведомлением (для диагностики)
    showSimpleNotification("FCM token (debug)", token)
  }

  override fun onMessageReceived(message: RemoteMessage) {
    val title = message.notification?.title ?: "WB Intercom"
    val body = message.notification?.body ?: (message.data["body"] ?: "Doorbell")
    showSimpleNotification(title, body)
  }

  private fun showSimpleNotification(title: String, body: String) {
    val channelId = "wb_intercom_push"

    val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      nm.createNotificationChannel(
        NotificationChannel(channelId, "WB Intercom Push", NotificationManager.IMPORTANCE_HIGH)
      )
    }

    val n = NotificationCompat.Builder(this, channelId)
      .setSmallIcon(android.R.drawable.stat_notify_more)
      .setContentTitle(title)
      .setContentText(body.take(120))
      .setStyle(NotificationCompat.BigTextStyle().bigText(body))
      .build()

    nm.notify(3001, n)
  }
}
