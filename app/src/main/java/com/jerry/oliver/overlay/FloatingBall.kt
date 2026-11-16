package com.jerry.oliver.overlay

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import com.jerry.oliver.util.dp
import kotlin.math.abs

/** Draggable ball (like the "G" ball). Tap = open / close the menu. */
class FloatingBall(
    context: Context,
    private val wm: WindowManager,
    private val onTap: () -> Unit,
) : View(context) {

    private val sizePx = context.dp(44)
    private val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#FF1A00") }
    private val ring = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE; style = Paint.Style.STROKE; strokeWidth = context.dp(2).toFloat()
    }
    private val text = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE; textAlign = Paint.Align.CENTER; textSize = context.dp(18).toFloat()
        isFakeBoldText = true
    }

    private val params = WindowManager.LayoutParams(
        sizePx, sizePx,
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        PixelFormat.TRANSLUCENT
    ).apply {
        gravity = Gravity.TOP or Gravity.START
        x = context.dp(8)
        y = context.dp(60)
    }

    private var startX = 0f
    private var startY = 0f
    private var originX = 0
    private var originY = 0
    private var moved = false

    override fun onTouchEvent(e: MotionEvent): Boolean {
        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = e.rawX; startY = e.rawY
                originX = params.x; originY = params.y
                moved = false
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = e.rawX - startX
                val dy = e.rawY - startY
                if (abs(dx) > 10 || abs(dy) > 10) moved = true
                params.x = originX + dx.toInt()
                params.y = originY + dy.toInt()
                wm.updateViewLayout(this, params)
            }
            MotionEvent.ACTION_UP -> if (!moved) onTap()
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        val c = width / 2f
        canvas.drawCircle(c, c, c - 2f, fill)
        canvas.drawCircle(c, c, c - 2f, ring)
        canvas.drawText("O", c, c + text.textSize / 3f, text)
    }

    fun show() = wm.addView(this, params)

    fun hide() {
        runCatching { wm.removeView(this) }
    }
}
