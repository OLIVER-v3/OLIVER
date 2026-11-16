package com.jerry.oliver.editor

import android.app.AlertDialog
import android.content.Context
import android.view.Gravity
import android.view.InputDevice
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.jerry.oliver.keymap.Binding
import com.jerry.oliver.keymap.ConflictChecker
import com.jerry.oliver.keymap.Fn
import com.jerry.oliver.keymap.GameButton
import com.jerry.oliver.keymap.KeyCatalog
import com.jerry.oliver.keymap.Profile

/** Lets the user press ANY key / mouse button / wheel and attach a function to a game button. */
object BindingDialog {

    fun show(context: Context, profile: Profile, button: GameButton, onDone: () -> Unit) {
        val dp = context.resources.displayMetrics.density
        fun pad(v: Int) = (v * dp).toInt()

        var captured: String? = null

        val list = TextView(context).apply { textSize = 14f }
        fun refreshList() {
            list.text = if (button.bindings.isEmpty()) "No keys bound yet."
            else button.bindings.joinToString("\n") { "• ${it.input}  →  ${it.fn.label}" }
        }
        refreshList()

        val capture = TextView(context).apply {
            text = "Tap here, then press a key / click / scroll"
            gravity = Gravity.CENTER
            textSize = 16f
            setPadding(pad(12), pad(20), pad(12), pad(20))
            setBackgroundColor(0x33FF1A00)
            isFocusable = true
            isFocusableInTouchMode = true
        }
        fun setCaptured(name: String) {
            captured = name
            capture.text = "Captured:  $name"
        }
        capture.setOnClickListener { capture.requestFocus() }
        capture.setOnKeyListener { _, _, event: KeyEvent ->
            if (event.action == KeyEvent.ACTION_DOWN && event.repeatCount == 0) {
                setCaptured(KeyCatalog.combo(event)); true
            } else true
        }
        capture.setOnTouchListener { v, event: MotionEvent ->
            if (event.getToolType(0) == MotionEvent.TOOL_TYPE_MOUSE &&
                event.actionMasked == MotionEvent.ACTION_DOWN
            ) {
                KeyCatalog.mouseButtonsOf(event).firstOrNull()?.let(::setCaptured)
                true
            } else {
                v.requestFocus(); false
            }
        }
        capture.setOnGenericMotionListener { _, event: MotionEvent ->
            if (event.isFromSource(InputDevice.SOURCE_MOUSE) &&
                event.actionMasked == MotionEvent.ACTION_SCROLL
            ) {
                val up = event.getAxisValue(MotionEvent.AXIS_VSCROLL) > 0
                setCaptured(if (up) KeyCatalog.WHEEL_UP else KeyCatalog.WHEEL_DOWN)
                true
            } else false
        }

        val fnSpinner = Spinner(context).apply {
            adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item,
                Fn.values().map { it.label })
        }

        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(pad(20), pad(12), pad(20), 0)
            addView(list)
            addView(View(context), LinearLayout.LayoutParams(1, pad(12)))
            addView(capture)
            addView(View(context), LinearLayout.LayoutParams(1, pad(12)))
            addView(TextView(context).apply { text = "Function"; textSize = 12f })
            addView(fnSpinner)
        }

        val dialog = AlertDialog.Builder(context)
            .setTitle("Bind keys: ${button.id}")
            .setView(layout)
            .setPositiveButton("Add binding", null)
            .setNeutralButton("Clear all", null)
            .setNegativeButton("Done") { _, _ -> onDone() }
            .create()
        dialog.setOnDismissListener { onDone() }
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val input = captured
            if (input == null) {
                Toast.makeText(context, "Press a key first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (ConflictChecker.isConflicting(profile, input, button.id)) {
                Toast.makeText(context, "Warning: $input is already used on another button",
                    Toast.LENGTH_LONG).show()
            }
            button.bindings += Binding(input, Fn.values()[fnSpinner.selectedItemPosition])
            captured = null
            capture.text = "Tap here, then press a key / click / scroll"
            refreshList()
        }
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
            button.bindings.clear(); refreshList()
        }
    }
}
