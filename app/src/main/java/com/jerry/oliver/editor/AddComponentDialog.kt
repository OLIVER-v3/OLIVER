package com.jerry.oliver.editor

import android.app.AlertDialog
import android.content.Context
import com.jerry.oliver.components.ComponentType

object AddComponentDialog {
    fun show(context: Context, onPick: (ComponentType) -> Unit) {
        val types = ComponentType.values()
        AlertDialog.Builder(context)
            .setTitle("Add component")
            .setItems(types.map { it.label }.toTypedArray()) { _, i -> onPick(types[i]) }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
