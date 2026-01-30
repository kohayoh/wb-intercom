package com.kolia.wbintercom

import android.content.Context
import android.util.Log
import org.eclipse.paho.client.mqttv3.*
import java.util.UUID

class MqttManager(
  private val ctx: Context,
  private val onDoorbell: () -> Unit
) {
  private var client: MqttClient? = null

  fun start() {
    val p = Prefs(ctx)
    val uri = "tcp://${p.mqttHost}:${p.mqttPort}"

    try {
      client = MqttClient(uri, "wb-intercom-${UUID.randomUUID()}", null)

      val opt = MqttConnectOptions().apply {
        isAutomaticReconnect = true
        isCleanSession = true
        connectionTimeout = 5
        keepAliveInterval = 20
      }

      client?.setCallback(object : MqttCallback {
        override fun connectionLost(cause: Throwable?) {
          Log.w("MQTT", "lost: ${cause?.message}")
        }

        override fun messageArrived(topic: String?, message: MqttMessage?) {
          if (topic == p.topicDoorbell) {
            onDoorbell()
          }
        }

        override fun deliveryComplete(token: IMqttDeliveryToken?) {}
      })

      client?.connect(opt)
      client?.subscribe(p.topicDoorbell, 1)

      Log.i("MQTT", "connected $uri, sub ${p.topicDoorbell}")
    } catch (e: Exception) {
      Log.e("MQTT", "start error: ${e.message}", e)
    }
  }

  fun stop() {
    try {
      client?.disconnect()
    } catch (_: Exception) {}
    try {
      client?.close()
    } catch (_: Exception) {}
    client = null
  }
}
