package com.jerry.oliver.editor

import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.jerry.oliver.keymap.ConflictChecker
import com.jerry.oliver.keymap.KeymapStore

class EditorActivity : AppCompatActivity() {

    private lateinit var store: KeymapStore
    private lateinit var editor: EditorView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        store = KeymapStore(this)
        val profile = store.load(PROFILE)
        editor = EditorView(this, profile).apply {
            onBindRequest = { b ->
                BindingDialog.show(this@EditorActivity, profile, b) { invalidate() }
            }
        }

        val bar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            addView(Button(context).apply {
                text = "Save"
                setOnClickListener {
                    store.save(profile)
                    Toast.makeText(context, "Saved ${profile.buttons.size} buttons", Toast.LENGTH_SHORT).show()
                }
            })
            addView(Button(context).apply {
                text = "Conflicts"
                setOnClickListener {
                    val c = ConflictChecker.find(profile)
                    val msg = if (c.isEmpty()) "No conflicts"
                    else c.joinToString("\n") { "${it.input}: ${it.buttonIds.joinToString()}" }
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                }
            })
            addView(Button(context).apply {
                text = "Exit"
                setOnClickListener { store.save(profile); finish() }
            })
        }

        setContentView(FrameLayout(this).apply {
            addView(editor, FrameLayout.LayoutParams(-1, -1))
            addView(bar, FrameLayout.LayoutParams(-2, -2, Gravity.TOP or Gravity.END))
        })
    }

    override fun onPause() {
        super.onPause()
        store.save(editor.profile)
    }

    companion object {
        const val PROFILE = "freefire"
    }
}
