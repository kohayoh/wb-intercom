package com.kolia.wbintercom

import android.content.Context

class Prefs(ctx: Context) {
  private val sp = ctx.getSharedPreferences("wb_intercom", Context.MODE_PRIVATE)

  var mqttHost: String
    get() = sp.getString("mqtt_host", "192.168.1.18") ?: "192.168.1.18"
    set(v) = sp.edit().putString("mqtt_host", v).apply()

  var mqttPort: Int
    get() = sp.getInt("mqtt_port", 1883)
    set(v) = sp.edit().putInt("mqtt_port", v).apply()

  var topicDoorbell: String
    get() = sp.getString("topic_doorbell", "wb/intercom/doorbell") ?: "wb/intercom/doorbell"
    set(v) = sp.edit().putString("topic_doorbell", v).apply()
}
