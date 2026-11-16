package com.jerry.oliver.ui.games

import android.app.AlertDialog
import android.content.Context
import com.jerry.oliver.games.GameRepository
import com.jerry.oliver.games.InstalledApps

object AddGameDialog {
    fun show(context: Context, repo: GameRepository, onAdded: () -> Unit) {
        val apps = InstalledApps.list(context)
        AlertDialog.Builder(context)
            .setTitle("Add Game")
            .setItems(apps.map { it.label }.toTypedArray()) { _, i ->
                repo.add(apps[i].pkg)
                onAdded()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
