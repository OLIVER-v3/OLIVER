package com.jerry.oliver.monitor

import android.content.Context
import android.os.BatteryManager

object BatteryMonitor {
    fun percent(context: Context): Int {
        val bm = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    }
}
