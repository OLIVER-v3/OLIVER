package com.jerry.oliver.session

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.ContextCompat

object GameLauncher {

    /** Starts the OLIVER overlay session, then opens the game. */
    fun start(context: Context, pkg: String) {
        if (!Settings.canDrawOverlays(context)) {
            Toast.makeText(context, "Allow “Display over other apps” for OLIVER, then tap Start Game again",
                Toast.LENGTH_LONG).show()
            context.startActivity(
                Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}"))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
            return
        }
        val launch = context.packageManager.getLaunchIntentForPackage(pkg)
        if (launch == null) {
            Toast.makeText(context, "Game not found", Toast.LENGTH_SHORT).show()
            return
        }
        ContextCompat.startForegroundService(
            context,
            Intent(context, SessionService::class.java).putExtra(SessionService.EXTRA_PKG, pkg)
        )
        context.startActivity(launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}
