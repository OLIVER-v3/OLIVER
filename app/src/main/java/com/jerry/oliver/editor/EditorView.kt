package com.jerry.oliver.editor

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.jerry.oliver.components.ComponentType
import com.jerry.oliver.keymap.GameButton
import com.jerry.oliver.keymap.Profile
import kotlin.math.hypot

/**
 * Canvas where components are placed.
 *  - "Add component" -> pick a type -> tap the screen to place it
 *  - tap a component  -> edit its key bindings
 *  - drag a component -> move it
 *  - long-press       -> delete it
 */
class EditorView(context: Context, var profile: Profile) : View(context) {

    var onBindRequest: ((GameButton) -> Unit)? = null
    var onChanged: (() -> Unit)? = null

    /** Type chosen in "Add component"; the next tap on empty space places it. */
    var pendingType: ComponentType? = null

    private val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.argb(120, 255, 26, 0) }
    private val stroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FF1A00"); style = Paint.Style.STROKE; strokeWidth = 4f
    }
    private val text = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE; textAlign = Paint.Align.CENTER; textSize = 34f
    }
    private val viewFill = Paint().apply { color = Color.argb(40, 255, 255, 255) }
    private val hint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(150, 255, 255, 255); textAlign = Paint.Align.CENTER; textSize = 40f
    }

    private var dragging: GameButton? = null

    private val detector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            dragging = hit(e.x, e.y)
            return true
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            val b = hit(e.x, e.y)
            if (b != null) {
                onBindRequest?.invoke(b)
                return true
            }
            val type = pendingType
            if (type == null) {
                Toast.makeText(context, "Tap “Add component” first", Toast.LENGTH_SHORT).show()
            } else if (width > 0 && height > 0) {
                var id = "${type.name.lowercase()}_${profile.buttons.size + 1}"
                while (profile.buttons.any { it.id == id }) id += "x"
                val nb = GameButton(id, e.x / width, e.y / height, size = type.defaultSize, type = type)
                profile.buttons += nb
                pendingType = null
                invalidate(); onChanged?.invoke()
                onBindRequest?.invoke(nb)
            }
            return true
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent, dx: Float, dy: Float): Boolean {
            dragging?.let {
                it.x = (e2.x / width).coerceIn(0f, 1f)
                it.y = (e2.y / height).coerceIn(0f, 1f)
                invalidate(); onChanged?.invoke()
            }
            return true
        }

        override fun onLongPress(e: MotionEvent) {
            hit(e.x, e.y)?.let {
                profile.buttons.remove(it)
                dragging = null
                invalidate(); onChanged?.invoke()
            }
        }
    })

    private fun radius(b: GameButton) = b.size * width / 2f

    private fun hit(px: Float, py: Float): GameButton? =
        profile.buttons.lastOrNull {
            hypot(px - it.x * width, py - it.y * height) <= radius(it)
        }

    override fun onTouchEvent(event: MotionEvent): Boolean = detector.onTouchEvent(event)

    override fun onDraw(canvas: Canvas) {
        val v = profile.view
        val r = RectF(v.x * width, v.y * height, (v.x + v.w) * width, (v.y + v.h) * height)
        canvas.drawRect(r, viewFill)
        canvas.drawText("VIEW (camera look)", r.centerX(), r.centerY(), hint)

        for (b in profile.buttons) {
            val cx = b.x * width
            val cy = b.y * height
            val rad = radius(b)
            canvas.drawCircle(cx, cy, rad, fill)
            canvas.drawCircle(cx, cy, rad, stroke)
            val label = b.label.ifEmpty { b.bindings.firstOrNull()?.input ?: b.type.short.ifEmpty { b.id } }
            canvas.drawText(label.take(8), cx, cy + text.textSize / 3, text)
        }
    }
}
