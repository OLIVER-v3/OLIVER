package com.jerry.oliver.keymap

/** Finds the same input bound to more than one button. */
object ConflictChecker {

    data class Conflict(val input: String, val buttonIds: List<String>)

    fun find(profile: Profile): List<Conflict> {
        val byInput = mutableMapOf<String, MutableSet<String>>()
        for (b in profile.buttons) for (bind in b.bindings) {
            byInput.getOrPut(bind.input) { linkedSetOf() }.add(b.id)
        }
        return byInput.filter { it.value.size > 1 }.map { Conflict(it.key, it.value.toList()) }
    }

    fun isConflicting(profile: Profile, input: String, exceptButtonId: String): Boolean =
        profile.buttons.any { it.id != exceptButtonId && it.bindings.any { b -> b.input == input } }
}
