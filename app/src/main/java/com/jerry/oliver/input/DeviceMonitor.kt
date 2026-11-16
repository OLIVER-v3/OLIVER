package com.jerry.oliver.input

import android.view.InputDevice

/** Tells whether a physical mouse / keyboard is connected. */
object DeviceMonitor {

    data class State(val mouse: Boolean, val keyboard: Boolean)

    fun read(): State {
        var mouse = false
        var keyboard = false
        for (id in InputDevice.getDeviceIds()) {
            val d = InputDevice.getDevice(id) ?: continue
            if (d.isVirtual) continue
            val s = d.sources
            if (s and InputDevice.SOURCE_MOUSE == InputDevice.SOURCE_MOUSE) mouse = true
            if (s and InputDevice.SOURCE_KEYBOARD == InputDevice.SOURCE_KEYBOARD &&
                d.keyboardType == InputDevice.KEYBOARD_TYPE_ALPHABETIC
            ) keyboard = true
        }
        return State(mouse, keyboard)
    }
}
