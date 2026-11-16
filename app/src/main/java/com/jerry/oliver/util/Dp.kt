package com.jerry.oliver.util

import android.content.Context

fun Context.dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()
