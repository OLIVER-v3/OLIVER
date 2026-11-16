package com.jerry.oliver.overlay

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView
import com.jerry.oliver.monitor.BatteryMonitor
import com.jerry.oliver.util.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Small status strip at the top of the game: battery, session time, clock. */
class HudBar(private val ctx: Context, private val wm: WindowManager) {

    private val handler = Handler(Looper.getMainLooper())
    private val startedAt = SystemClock.elapsedRealtime()
    private val clock = SimpleDateFormat("HH:mm", Locale.getDefault())

    private val view = TextView(ctx).apply {
        setTextColor(Color.WHITE)
        textSize = 12f
        setPadding(ctx.dp(12), ctx.dp(4), ctx.dp(12), ctx.dp(4))
        background = GradientDrawable().apply {
            cornerRadius = ctx.dp(14).toFloat()
            setColor(Color.argb(150, 0, 0, 0))
        }
    }

    private val tick = object : Runnable {
        override fun run() {
            val secs = (SystemClock.elapsedRealtime() - startedAt) / 1000
            view.text = "%d%%  ·  %02d:%02d:%02d  ·  %s".format(
                BatteryMonitor.percent(ctx), secs / 3600, secs / 60 % 60, secs % 60,
                clock.format(Date())
            )
            handler.postDelayed(this, 1000)
        }
    }

    fun show() {
        val p = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            y = ctx.dp(6)
        }
        wm.addView(view, p)
        tick.run()
    }

    fun hide() {
        handler.removeCallbacks(tick)
        runCatching { wm.removeView(view) }
    }
}
