package com.jerry.oliver.input

import com.jerry.oliver.keymap.ViewSettings

/**
 * Turns relative mouse movement into a continuous one-finger swipe inside the View region.
 * The virtual finger starts at the region centre and is lifted + re-placed when it nears
 * the edge, so the camera can keep turning forever.
 *
 * This class only computes points; the injection backend (Phase 2) sends them to the game.
 */
class CameraLookHandler(
    private val view: ViewSettings,
    private val screenW: Int,
    private val screenH: Int,
) {
    sealed interface Action {
        data class Down(val x: Float, val y: Float) : Action
        data class Move(val x: Float, val y: Float) : Action
        data object Up : Action
    }

    private val left = view.x * screenW
    private val top = view.y * screenH
    private val right = left + view.w * screenW
    private val bottom = top + view.h * screenH
    private val cx = (left + right) / 2f
    private val cy = (top + bottom) / 2f
    private val margin = 0.12f

    private var active = false
    private var px = cx
    private var py = cy

    /** dx/dy are raw mouse deltas in pixels. Returns the touch actions to inject. */
    fun onMouseDelta(dx: Float, dy: Float): List<Action> {
        val out = mutableListOf<Action>()
        if (!active) {
            px = cx; py = cy; active = true
            out += Action.Down(px, py)
        }
        px += dx * view.sensX
        py += (if (view.invertY) -dy else dy) * view.sensY

        val mx = (right - left) * margin
        val my = (bottom - top) * margin
        if (px < left + mx || px > right - mx || py < top + my || py > bottom - my) {
            out += Action.Move(px.coerceIn(left, right), py.coerceIn(top, bottom))
            out += Action.Up
            active = false
        } else {
            out += Action.Move(px, py)
        }
        return out
    }

    fun release(): List<Action> =
        if (active) { active = false; listOf(Action.Up) } else emptyList()
}
