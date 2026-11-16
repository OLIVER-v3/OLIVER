package com.jerry.oliver.ui.games

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.hardware.input.InputManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.jerry.oliver.editor.EditorActivity
import com.jerry.oliver.games.GameRepository
import com.jerry.oliver.games.InstalledApps
import com.jerry.oliver.keymap.KeymapRepository
import com.jerry.oliver.session.GameLauncher
import com.jerry.oliver.ui.status.ConnectionBadges
import com.jerry.oliver.util.dp

class GamesFragment : Fragment() {

    private lateinit var repo: GameRepository
    private lateinit var row: LinearLayout
    private lateinit var badges: ConnectionBadges
    private var selected: String? = null

    private val deviceListener = object : InputManager.InputDeviceListener {
        override fun onInputDeviceAdded(deviceId: Int) { badges.refresh() }
        override fun onInputDeviceRemoved(deviceId: Int) { badges.refresh() }
        override fun onInputDeviceChanged(deviceId: Int) { badges.refresh() }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View {
        val ctx = requireContext()
        repo = GameRepository(ctx)
        badges = ConnectionBadges(ctx)

        val title = TextView(ctx).apply {
            text = "Games"
            textSize = 26f
            setTextColor(Color.WHITE)
            setTypeface(typeface, android.graphics.Typeface.BOLD)
        }
        val header = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            addView(title, LinearLayout.LayoutParams(0, -2, 1f))
            addView(badges.view)
        }

        val add = MaterialButton(ctx).apply {
            text = "+ Add Game"
            setOnClickListener { AddGameDialog.show(ctx, repo) { render() } }
        }

        row = LinearLayout(ctx).apply { orientation = LinearLayout.HORIZONTAL }
        val scroll = HorizontalScrollView(ctx).apply { addView(row) }

        val start = MaterialButton(ctx).apply {
            text = "Start Game"
            setOnClickListener {
                selected?.let { GameLauncher.start(ctx, it) }
                    ?: Toast.makeText(ctx, "Select a game first", Toast.LENGTH_SHORT).show()
            }
        }
        val edit = MaterialButton(ctx, null, com.google.android.material.R.attr.materialButtonOutlinedStyle).apply {
            text = "Edit keymap"
            setOnClickListener {
                val pkg = selected
                if (pkg == null) {
                    Toast.makeText(ctx, "Select a game first", Toast.LENGTH_SHORT).show()
                } else {
                    startActivity(Intent(ctx, EditorActivity::class.java)
                        .putExtra(EditorActivity.EXTRA_PROFILE, KeymapRepository.profileNameFor(pkg))
                        .putExtra(EditorActivity.EXTRA_PRESET, KeymapRepository.presetFor(pkg)))
                }
            }
        }
        val actions = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            addView(start, LinearLayout.LayoutParams(0, -2, 1f).apply { marginEnd = ctx.dp(8) })
            addView(edit, LinearLayout.LayoutParams(0, -2, 1f))
        }

        val root = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(ctx.dp(16), ctx.dp(16), ctx.dp(16), ctx.dp(8))
            addView(header)
            addView(add, LinearLayout.LayoutParams(-2, -2).apply { topMargin = ctx.dp(12) })
            addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f).apply { topMargin = ctx.dp(12) })
            addView(actions)
        }
        render()
        badges.refresh()
        return root
    }

    private fun render() {
        val ctx = requireContext()
        row.removeAllViews()
        val games = repo.list()
        if (selected !in games) selected = games.firstOrNull()
        if (games.isEmpty()) {
            row.addView(TextView(ctx).apply {
                text = "No games yet. Tap “Add Game”."
                setTextColor(Color.GRAY)
                setPadding(ctx.dp(8), ctx.dp(24), 0, 0)
            })
        }
        for (pkg in games) row.addView(card(ctx, pkg))
    }

    private fun card(ctx: Context, pkg: String): View {
        val isSel = pkg == selected
        val icon = ImageView(ctx).apply {
            runCatching { setImageDrawable(ctx.packageManager.getApplicationIcon(pkg)) }
        }
        val label = TextView(ctx).apply {
            text = InstalledApps.labelOf(ctx, pkg)
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            maxLines = 2
            textSize = 14f
        }
        return LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(ctx.dp(12), ctx.dp(16), ctx.dp(12), ctx.dp(12))
            background = GradientDrawable().apply {
                cornerRadius = ctx.dp(14).toFloat()
                setColor(Color.parseColor("#1B1B1F"))
                setStroke(ctx.dp(if (isSel) 3 else 1), if (isSel) Color.parseColor("#FF1A00") else Color.DKGRAY)
            }
            addView(icon, LinearLayout.LayoutParams(ctx.dp(72), ctx.dp(72)))
            addView(label, LinearLayout.LayoutParams(-1, -2).apply { topMargin = ctx.dp(8) })
            layoutParams = LinearLayout.LayoutParams(ctx.dp(130), -2).apply { marginEnd = ctx.dp(12) }
            setOnClickListener { selected = pkg; render() }
            setOnLongClickListener {
                AlertDialog.Builder(ctx)
                    .setMessage("Remove ${InstalledApps.labelOf(ctx, pkg)} from OLIVER?")
                    .setPositiveButton("Remove") { _, _ -> repo.remove(pkg); render() }
                    .setNegativeButton("Cancel", null)
                    .show()
                true
            }
        }
    }

    override fun onStart() {
        super.onStart()
        requireContext().getSystemService(InputManager::class.java)
            .registerInputDeviceListener(deviceListener, Handler(Looper.getMainLooper()))
        badges.refresh()
    }

    override fun onStop() {
        requireContext().getSystemService(InputManager::class.java)
            .unregisterInputDeviceListener(deviceListener)
        super.onStop()
    }
}
