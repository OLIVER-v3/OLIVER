package com.jerry.oliver.overlay

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import com.jerry.oliver.util.dp

/** In-game side menu: Home, Edit keymap, Keymap configs, Keyboard typing, Settings, Close. */
class RadialMenu(
    private val ctx: Context,
    private val wm: WindowManager,
    private val onAction: (Action) -> Unit,
) {
    enum class Action(val label: String) {
        HOME("Home"),
        EDIT("Edit keymap"),
        CONFIGS("Keymap configs"),
        TYPING("Keyboard typing"),
        SETTINGS("Settings"),
        CLOSE("Close session")
    }

    private var shown = false

    private val panel = LinearLayout(ctx).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(ctx.dp(8), ctx.dp(8), ctx.dp(8), ctx.dp(8))
        background = GradientDrawable().apply {
            cornerRadius = ctx.dp(16).toFloat()
            setColor(Color.argb(230, 27, 27, 31))
            setStroke(ctx.dp(1), Color.parseColor("#FF1A00"))
        }
        for (a in Action.values()) {
            addView(TextView(ctx).apply {
                text = a.label
                textSize = 15f
                setTextColor(Color.WHITE)
                setPadding(ctx.dp(16), ctx.dp(10), ctx.dp(16), ctx.dp(10))
                setOnClickListener { onAction(a) }
            })
        }
    }

    fun toggle() = if (shown) hide() else show()

    fun show() {
        if (shown) return
        val p = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.START or Gravity.CENTER_VERTICAL
            x = ctx.dp(64)
        }
        wm.addView(panel, p)
        shown = true
    }

    fun hide() {
        if (!shown) return
        runCatching { wm.removeView(panel) }
        shown = false
    }
}
