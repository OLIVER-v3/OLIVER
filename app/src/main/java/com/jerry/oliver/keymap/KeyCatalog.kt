package com.jerry.oliver.keymap

import android.view.KeyEvent
import android.view.MotionEvent

/** Names for every input OLIVER can bind: any keyboard key, mouse button, wheel direction. */
object KeyCatalog {

    const val LMB = "LMB"
    const val RMB = "RMB"
    const val MMB = "MMB"
    const val MOUSE_BACK = "MOUSE_BACK"
    const val MOUSE_FORWARD = "MOUSE_FORWARD"
    const val WHEEL_UP = "WHEEL_UP"
    const val WHEEL_DOWN = "WHEEL_DOWN"

    val mouseInputs = listOf(LMB, RMB, MMB, MOUSE_BACK, MOUSE_FORWARD, WHEEL_UP, WHEEL_DOWN)

    /** KEYCODE_A -> "A", KEYCODE_SHIFT_LEFT -> "SHIFT_LEFT", gamepad buttons included. */
    fun nameOf(keyCode: Int): String =
        KeyEvent.keyCodeToString(keyCode).removePrefix("KEYCODE_")

    /** Name -> Android key code, or null if it is a mouse input / unknown. */
    fun keyCodeOf(name: String): Int? {
        val code = KeyEvent.keyCodeFromString("KEYCODE_$name")
        return if (code == KeyEvent.KEYCODE_UNKNOWN) null else code
    }

    /** Mouse buttons that are pressed in this event. */
    fun mouseButtonsOf(event: MotionEvent): List<String> {
        val s = event.buttonState
        val out = mutableListOf<String>()
        if (s and MotionEvent.BUTTON_PRIMARY != 0) out += LMB
        if (s and MotionEvent.BUTTON_SECONDARY != 0) out += RMB
        if (s and MotionEvent.BUTTON_TERTIARY != 0) out += MMB
        if (s and MotionEvent.BUTTON_BACK != 0) out += MOUSE_BACK
        if (s and MotionEvent.BUTTON_FORWARD != 0) out += MOUSE_FORWARD
        return out
    }

    /** Join modifiers + key, e.g. "SHIFT+E". */
    fun combo(event: KeyEvent): String {
        val parts = mutableListOf<String>()
        if (event.isCtrlPressed) parts += "CTRL"
        if (event.isAltPressed) parts += "ALT"
        if (event.isShiftPressed) parts += "SHIFT"
        val key = nameOf(event.keyCode)
        val isModifier = event.keyCode in listOf(
            KeyEvent.KEYCODE_CTRL_LEFT, KeyEvent.KEYCODE_CTRL_RIGHT,
            KeyEvent.KEYCODE_ALT_LEFT, KeyEvent.KEYCODE_ALT_RIGHT,
            KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.KEYCODE_SHIFT_RIGHT,
        )
        // A lone modifier press binds the modifier itself (e.g. SHIFT for sprint).
        return if (isModifier) key else (parts + key).joinToString("+")
    }
}
