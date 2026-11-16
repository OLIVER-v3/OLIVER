package com.jerry.oliver.editor

import android.content.ClipboardManager
import android.content.Intent
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
import com.jerry.oliver.keymap.KeymapStore
import com.jerry.oliver.keymap.Profile

/** Edit keymap screen. Toolbar: Exit · Add component · Save · Share · Import. */
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
        val name = intent.getStringExtra(EXTRA_PROFILE) ?: DEFAULT_PROFILE
        val preset = intent.getStringExtra(EXTRA_PRESET) ?: if (name == DEFAULT_PROFILE) DEFAULT_PROFILE else null
        val profile = store.load(name, preset)

        editor = EditorView(this, profile).apply {
            onBindRequest = { b ->
                BindingDialog.show(this@EditorActivity, this.profile, b) { invalidate() }
            }
        }

        fun tool(label: String, action: () -> Unit) = Button(this).apply {
            text = label
            setOnClickListener { action() }
        }

        val bar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            addView(tool("Exit") { store.save(editor.profile); finish() })
            addView(tool("Add component") {
                AddComponentDialog.show(this@EditorActivity) { type ->
                    editor.pendingType = type
                    Toast.makeText(this@EditorActivity, "Tap the screen to place ${type.label}",
                        Toast.LENGTH_SHORT).show()
                }
            })
            addView(tool("Save") {
                store.save(editor.profile)
                Toast.makeText(this@EditorActivity, "Saved ${editor.profile.buttons.size} components",
                    Toast.LENGTH_SHORT).show()
            })
            addView(tool("Share") {
                val send = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, editor.profile.toJsonString())
                }
                startActivity(Intent.createChooser(send, "Share keymap"))
            })
            addView(tool("Import") { importFromClipboard() })
        }

        setContentView(FrameLayout(this).apply {
            addView(editor, FrameLayout.LayoutParams(-1, -1))
            addView(bar, FrameLayout.LayoutParams(-2, -2, Gravity.TOP or Gravity.CENTER_HORIZONTAL))
        })
    }

    private fun importFromClipboard() {
        val cm = getSystemService(ClipboardManager::class.java)
        val text = cm.primaryClip?.takeIf { it.itemCount > 0 }?.getItemAt(0)?.text?.toString()
        val parsed = runCatching { Profile.fromJsonString(text ?: "") }.getOrNull()
        if (parsed == null) {
            Toast.makeText(this, "Copy a keymap JSON first", Toast.LENGTH_SHORT).show()
            return
        }
        val p = editor.profile
        p.buttons.clear()
        p.buttons.addAll(parsed.buttons)
        p.view = parsed.view
        editor.invalidate()
        Toast.makeText(this, "Imported ${parsed.buttons.size} components", Toast.LENGTH_SHORT).show()
    }

    override fun onPause() {
        super.onPause()
        store.save(editor.profile)
    }

    companion object {
        const val EXTRA_PROFILE = "profile"
        const val EXTRA_PRESET = "preset"
        const val DEFAULT_PROFILE = "freefire"
    }
}
