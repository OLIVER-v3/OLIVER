package com.jerry.oliver.keymap

import android.content.Context
import java.io.File

/** Saves profiles as JSON in app storage; first run copies a bundled preset if one exists. */
class KeymapStore(private val context: Context) {

    private val dir get() = File(context.filesDir, "profiles").apply { mkdirs() }

    fun file(name: String) = File(dir, "$name.json")

    fun load(name: String, preset: String? = null): Profile {
        val f = file(name)
        if (!f.exists()) {
            val src = preset ?: name
            val bundled = runCatching {
                context.assets.open("profiles/$src.json").bufferedReader().use { it.readText() }
            }.getOrNull() ?: return Profile(name)
            f.writeText(bundled)
        }
        return Profile.fromJsonString(f.readText()).also { it.name = name }
    }

    fun save(profile: Profile) {
        file(profile.name).writeText(profile.toJsonString())
    }
}
