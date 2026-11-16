package com.jerry.oliver.games

import android.content.Context

/** The games the user added to OLIVER (package names). */
class GameRepository(context: Context) {
    private val prefs = context.getSharedPreferences("games", Context.MODE_PRIVATE)

    fun list(): List<String> = prefs.getStringSet(KEY, emptySet())!!.toList().sorted()

    fun add(pkg: String) = save(prefs.getStringSet(KEY, emptySet())!!.toMutableSet().apply { add(pkg) })

    fun remove(pkg: String) = save(prefs.getStringSet(KEY, emptySet())!!.toMutableSet().apply { remove(pkg) })

    private fun save(set: Set<String>) {
        prefs.edit().putStringSet(KEY, set).apply()
    }

    private companion object {
        const val KEY = "packages"
    }
}
