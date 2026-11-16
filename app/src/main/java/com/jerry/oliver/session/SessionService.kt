package com.jerry.oliver.session

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.ServiceCompat
import com.jerry.oliver.overlay.OverlayManager

/** Foreground service that keeps the OLIVER overlay alive on top of the game. */
class SessionService : Service() {

    private var overlay: OverlayManager? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val pkg = intent?.getStringExtra(EXTRA_PKG)
        if (pkg == null) {
            stopSelf()
            return START_NOT_STICKY
        }
        startAsForeground()
        overlay?.hide()
        overlay = OverlayManager(this, pkg) { stopSelf() }.also { it.show() }
        return START_NOT_STICKY
    }

    private fun startAsForeground() {
        val nm = getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(
            NotificationChannel(CHANNEL, "OLIVER session", NotificationManager.IMPORTANCE_LOW)
        )
        val notification = Notification.Builder(this, CHANNEL)
            .setContentTitle("OLIVER is running")
            .setContentText("Keyboard & mouse overlay active")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()
        val type = if (Build.VERSION.SDK_INT >= 34) ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE else 0
        ServiceCompat.startForeground(this, 1, notification, type)
    }

    override fun onDestroy() {
        overlay?.hide()
        overlay = null
        super.onDestroy()
    }

    companion object {
        const val EXTRA_PKG = "pkg"
        private const val CHANNEL = "oliver_session"
    }
}
