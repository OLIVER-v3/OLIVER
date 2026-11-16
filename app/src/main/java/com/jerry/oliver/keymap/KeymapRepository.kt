package com.jerry.oliver.keymap

/** One keymap per game, stored under the game's package name. */
object KeymapRepository {
    fun profileNameFor(pkg: String): String = pkg.replace('.', '_')

    /** Bundled preset to start from, or null for an empty keymap. */
    fun presetFor(pkg: String): String? = when {
        pkg.startsWith("com.dts.freefire") -> "freefire"
        else -> null
    }
}
