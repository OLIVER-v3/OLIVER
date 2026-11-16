package com.jerry.oliver.keymap

import android.content.Context
import java.io.File

/** Saves profiles as JSON in app storage; first run copies the bundled default. */
class KeymapStore(private val context: Context) {

    private val dir get() = File(context.filesDir, "profiles").apply { mkdirs() }

    fun file(name: String) = File(dir, "$name.json")

    fun load(name: String): Profile {
        val f = file(name)
        if (!f.exists()) {
            val bundled = runCatching {
                context.assets.open("profiles/$name.json").bufferedReader().use { it.readText() }
            }.getOrNull()
            if (bundled != null) f.writeText(bundled) else return Profile(name)
        }
        return Profile.fromJsonString(f.readText())
    }

    fun save(profile: Profile) {
        file(profile.name).writeText(profile.toJsonString())
    }
}
