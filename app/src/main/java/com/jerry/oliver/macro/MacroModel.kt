package com.jerry.oliver.macro

/** A macro is an ordered list of steps (tap a component, wait, hold, release). */
sealed interface MacroStep {
    data class Tap(val buttonId: String) : MacroStep
    data class Hold(val buttonId: String, val ms: Int) : MacroStep
    data class Wait(val ms: Int) : MacroStep
}

data class Macro(val id: String, val name: String, val steps: List<MacroStep>)
