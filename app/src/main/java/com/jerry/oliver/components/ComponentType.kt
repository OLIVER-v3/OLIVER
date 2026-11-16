package com.jerry.oliver.components

import com.jerry.oliver.keymap.Fn

/** The 10 component types that can be placed on a keymap ("Add component"). */
enum class ComponentType(
    val label: String,
    val short: String,
    val defaultSize: Float,
    val defaultFn: Fn,
) {
    TOUCH("Touch", "", 0.08f, Fn.TAP),
    SWIPE("Swipe", "SW", 0.08f, Fn.SWIPE),
    MOUSE_SCROLL("Mouse Scroll", "SCR", 0.08f, Fn.TAP),
    MACRO("Macro", "M", 0.08f, Fn.MACRO),
    JOYSTICK("Joystick", "JOY", 0.14f, Fn.JOYSTICK),
    FPS_VIEW("FPS View", "VIEW", 0.10f, Fn.CAMERA_LOOK),
    FREE_LOOK("Free Look", "LOOK", 0.08f, Fn.CAMERA_LOOK),
    MOUSE_PICK("Mouse Pick", "PICK", 0.08f, Fn.TAP),
    LONG_PRESS_GROUP("Long press group", "LP", 0.08f, Fn.LONG_PRESS),
    FLOATING_BALL("Floating ball display", "BALL", 0.07f, Fn.TOGGLE_POINTER);
}
