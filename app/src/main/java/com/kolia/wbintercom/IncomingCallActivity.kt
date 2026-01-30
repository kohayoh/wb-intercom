package com.kolia.wbintercom

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class IncomingCallActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Показ поверх блокировки + включить экран
    if (Build.VERSION.SDK_INT >= 27) {
      setShowWhenLocked(true)
      setTurnScreenOn(true)
    } else {
      @Suppress("DEPRECATION")
      window.addFlags(
        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
          WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
          WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
      )
    }

    setContentView(R.layout.activity_incoming_call)

    findViewById<Button>(R.id.btnDecline).setOnClickListener {
      finish()
    }

    findViewById<Button>(R.id.btnOpen).setOnClickListener {
      // позже сюда добавим publish "open door"
      finish()
    }
  }
}
