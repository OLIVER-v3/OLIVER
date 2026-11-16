package com.jerry.oliver.overlay

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.jerry.oliver.MainActivity
import com.jerry.oliver.editor.EditorActivity
import com.jerry.oliver.keymap.KeymapRepository

/** Owns every overlay window shown on top of the game. */
class OverlayManager(
    private val ctx: Context,
    private val pkg: String,
    private val onClose: () -> Unit,
) {
    private val wm = ctx.getSystemService(Context.WINDOW_SERVICE) as android.view.WindowManager
    private val hud = HudBar(ctx, wm)
    private val menu = RadialMenu(ctx, wm) { onMenuAction(it) }
    private val ball = FloatingBall(ctx, wm) { menu.toggle() }

    fun show() {
        hud.show()
        ball.show()
    }

    fun hide() {
        menu.hide()
        ball.hide()
        hud.hide()
    }

    private fun onMenuAction(action: RadialMenu.Action) {
        when (action) {
            RadialMenu.Action.EDIT -> {
                menu.hide()
                ctx.startActivity(
                    Intent(ctx, EditorActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra(EditorActivity.EXTRA_PROFILE, KeymapRepository.profileNameFor(pkg))
                        .putExtra(EditorActivity.EXTRA_PRESET, KeymapRepository.presetFor(pkg))
                )
            }
            RadialMenu.Action.HOME, RadialMenu.Action.SETTINGS -> {
                menu.hide()
                ctx.startActivity(Intent(ctx, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
            RadialMenu.Action.CONFIGS, RadialMenu.Action.TYPING ->
                Toast.makeText(ctx, "${action.label}: coming soon", Toast.LENGTH_SHORT).show()
            RadialMenu.Action.CLOSE -> onClose()
        }
    }
}
