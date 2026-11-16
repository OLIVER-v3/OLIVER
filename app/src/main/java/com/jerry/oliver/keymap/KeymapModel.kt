package com.jerry.oliver.keymap

import com.jerry.oliver.components.ComponentType
import org.json.JSONArray
import org.json.JSONObject

/** Everything a key/mouse input can do on a game button. */
enum class Fn(val label: String) {
    TAP("Tap"),
    HOLD("Hold"),
    TOGGLE("Toggle"),
    DOUBLE_TAP("Double tap"),
    LONG_PRESS("Long press"),
    SWIPE("Swipe"),
    JOYSTICK("Joystick (WASD)"),
    CAMERA_LOOK("View / Camera look"),
    FIRE_LOOK("Fire + look"),
    SCOPE("Scope (hold/toggle)"),
    TURBO("Turbo (auto-tap)"),
    MACRO("Macro"),
    MULTI_TOUCH("Multi-touch"),
    PROFILE_SWITCH("Switch profile"),
    TOGGLE_POINTER("Show/hide pointer"),
    PAUSE_MAPPING("Pause mapping");
}

data class Binding(
    val input: String,
    val fn: Fn = Fn.TAP,
    val intervalMs: Int = 60,
    val macroId: String? = null,
) {
    fun toJson() = JSONObject().apply {
        put("input", input)
        put("function", fn.name)
        put("intervalMs", intervalMs)
        macroId?.let { put("macroId", it) }
    }

    companion object {
        fun fromJson(o: JSONObject) = Binding(
            input = o.getString("input"),
            fn = runCatching { Fn.valueOf(o.optString("function", "TAP")) }.getOrDefault(Fn.TAP),
            intervalMs = o.optInt("intervalMs", 60),
            macroId = o.optString("macroId").ifEmpty { null },
        )
    }
}

/** A button placed on the game screen. x/y/size are 0..1 fractions of the screen. */
data class GameButton(
    val id: String,
    var x: Float,
    var y: Float,
    var size: Float = 0.08f,
    var label: String = "",
    val bindings: MutableList<Binding> = mutableListOf(),
    var type: ComponentType = ComponentType.TOUCH,
) {
    fun toJson() = JSONObject().apply {
        put("id", id)
        put("type", type.name)
        put("x", x.toDouble())
        put("y", y.toDouble())
        put("size", size.toDouble())
        put("label", label)
        put("bindings", JSONArray().also { a -> bindings.forEach { a.put(it.toJson()) } })
    }

    companion object {
        fun fromJson(o: JSONObject): GameButton {
            val list = mutableListOf<Binding>()
            o.optJSONArray("bindings")?.let { a ->
                for (i in 0 until a.length()) list += Binding.fromJson(a.getJSONObject(i))
            }
            return GameButton(
                id = o.getString("id"),
                x = o.getDouble("x").toFloat(),
                y = o.getDouble("y").toFloat(),
                size = o.optDouble("size", 0.08).toFloat(),
                label = o.optString("label", ""),
                bindings = list,
                type = runCatching { ComponentType.valueOf(o.optString("type", "TOUCH")) }
                    .getOrDefault(ComponentType.TOUCH),
            )
        }
    }
}

/** Region of the screen where mouse movement turns into camera swipes. */
data class ViewSettings(
    var x: Float = 0.5f,
    var y: Float = 0f,
    var w: Float = 0.5f,
    var h: Float = 1f,
    var sensX: Float = 1f,
    var sensY: Float = 1f,
    var invertY: Boolean = false,
    var toggleKey: String = "TAB",
    var releaseKey: String = "ALT_LEFT",
) {
    fun toJson() = JSONObject().apply {
        put("x", x.toDouble()); put("y", y.toDouble())
        put("w", w.toDouble()); put("h", h.toDouble())
        put("sensX", sensX.toDouble()); put("sensY", sensY.toDouble())
        put("invertY", invertY)
        put("toggleKey", toggleKey); put("releaseKey", releaseKey)
    }

    companion object {
        fun fromJson(o: JSONObject) = ViewSettings(
            x = o.optDouble("x", 0.5).toFloat(),
            y = o.optDouble("y", 0.0).toFloat(),
            w = o.optDouble("w", 0.5).toFloat(),
            h = o.optDouble("h", 1.0).toFloat(),
            sensX = o.optDouble("sensX", 1.0).toFloat(),
            sensY = o.optDouble("sensY", 1.0).toFloat(),
            invertY = o.optBoolean("invertY", false),
            toggleKey = o.optString("toggleKey", "TAB"),
            releaseKey = o.optString("releaseKey", "ALT_LEFT"),
        )
    }
}

data class Profile(
    var name: String,
    var view: ViewSettings = ViewSettings(),
    val buttons: MutableList<GameButton> = mutableListOf(),
) {
    fun toJson() = JSONObject().apply {
        put("profile", name)
        put("view", view.toJson())
        put("buttons", JSONArray().also { a -> buttons.forEach { a.put(it.toJson()) } })
    }

    fun toJsonString(): String = toJson().toString(2)

    companion object {
        fun fromJson(o: JSONObject): Profile {
            val btns = mutableListOf<GameButton>()
            o.optJSONArray("buttons")?.let { a ->
                for (i in 0 until a.length()) btns += GameButton.fromJson(a.getJSONObject(i))
            }
            return Profile(
                name = o.optString("profile", "default"),
                view = o.optJSONObject("view")?.let { ViewSettings.fromJson(it) } ?: ViewSettings(),
                buttons = btns,
            )
        }

        fun fromJsonString(s: String) = fromJson(JSONObject(s))
    }
}
