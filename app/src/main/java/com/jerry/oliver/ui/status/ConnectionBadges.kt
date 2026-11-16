package com.jerry.oliver.ui.status

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.jerry.oliver.input.DeviceMonitor
import com.jerry.oliver.util.dp

/** "Mouse: Connected / Disconnected" and the same for the keyboard. */
class ConnectionBadges(private val context: Context) {

    private val mouse = badge()
    private val keyboard = badge()

    val view = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        addView(mouse)
        addView(keyboard)
    }

    private fun badge() = TextView(context).apply {
        textSize = 12f
        setTextColor(Color.WHITE)
        setPadding(context.dp(10), context.dp(4), context.dp(10), context.dp(4))
        layoutParams = LinearLayout.LayoutParams(-2, -2).apply { marginStart = context.dp(6) }
    }

    private fun style(v: TextView, label: String, connected: Boolean) {
        v.text = "$label: " + if (connected) "Connected" else "Disconnected"
        v.background = GradientDrawable().apply {
            cornerRadius = context.dp(14).toFloat()
            setColor(if (connected) Color.parseColor("#2E7D32") else Color.parseColor("#4A4A8A"))
        }
    }

    fun refresh() {
        val s = DeviceMonitor.read()
        style(mouse, "Mouse", s.mouse)
        style(keyboard, "Keyboard", s.keyboard)
    }
}
