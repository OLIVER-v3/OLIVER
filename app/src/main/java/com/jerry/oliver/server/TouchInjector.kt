package com.jerry.oliver.server

import android.os.SystemClock
import android.view.InputDevice
import android.view.InputEvent
import android.view.MotionEvent
import java.lang.reflect.Method

/**
 * Injects multi-touch events through the hidden InputManager API.
 * Only works inside the process started by `app_process` with shell (ADB) permission.
 */
class TouchInjector {

    private val manager: Any
    private val inject: Method

    private val ids = ArrayList<Int>()
    private val xs = HashMap<Int, Float>()
    private val ys = HashMap<Int, Float>()
    private var downTime = 0L

    init {
        val cls = runCatching { Class.forName("android.hardware.input.InputManagerGlobal") }
            .getOrElse { Class.forName("android.hardware.input.InputManager") }
        manager = cls.getMethod("getInstance").invoke(null)!!
        inject = cls.getMethod("injectInputEvent", InputEvent::class.java, Int::class.javaPrimitiveType)
    }

    @Synchronized
    fun down(id: Int, x: Float, y: Float) {
        if (ids.isEmpty()) downTime = SystemClock.uptimeMillis()
        ids.add(id); xs[id] = x; ys[id] = y
        val index = ids.size - 1
        send(
            if (ids.size == 1) MotionEvent.ACTION_DOWN
            else MotionEvent.ACTION_POINTER_DOWN or (index shl MotionEvent.ACTION_POINTER_INDEX_SHIFT)
        )
    }

    @Synchronized
    fun move(id: Int, x: Float, y: Float) {
        if (!ids.contains(id)) return
        xs[id] = x; ys[id] = y
        send(MotionEvent.ACTION_MOVE)
    }

    @Synchronized
    fun up(id: Int) {
        val index = ids.indexOf(id)
        if (index < 0) return
        send(
            if (ids.size == 1) MotionEvent.ACTION_UP
            else MotionEvent.ACTION_POINTER_UP or (index shl MotionEvent.ACTION_POINTER_INDEX_SHIFT)
        )
        ids.removeAt(index); xs.remove(id); ys.remove(id)
    }

    private fun send(action: Int) {
        val n = ids.size
        val props = Array(n) { i ->
            MotionEvent.PointerProperties().apply { id = ids[i]; toolType = MotionEvent.TOOL_TYPE_FINGER }
        }
        val coords = Array(n) { i ->
            MotionEvent.PointerCoords().apply {
                x = xs[ids[i]] ?: 0f; y = ys[ids[i]] ?: 0f; pressure = 1f; size = 1f
            }
        }
        val ev = MotionEvent.obtain(
            downTime, SystemClock.uptimeMillis(), action, n, props, coords,
            0, 0, 1f, 1f, 0, 0, InputDevice.SOURCE_TOUCHSCREEN, 0
        )
        try {
            inject.invoke(manager, ev, 0)
        } finally {
            ev.recycle()
        }
    }
}
