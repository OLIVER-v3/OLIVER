package com.jerry.oliver.tools

/** Converts a desired in-game feel into a mouse sensitivity multiplier for the View region. */
object SensitivityConverter {
    /** mouseDpi: DPI of the mouse; baseDpi: reference (800). */
    fun multiplier(mouseDpi: Int, baseDpi: Int = 800): Float =
        if (mouseDpi <= 0) 1f else baseDpi.toFloat() / mouseDpi
}
