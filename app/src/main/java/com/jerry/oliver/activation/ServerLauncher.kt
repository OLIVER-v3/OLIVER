package com.jerry.oliver.activation

/** Shell command that starts the OLIVER server (run through adb shell). */
object ServerLauncher {
    fun shellCommand(apkPath: String) =
        "CLASSPATH=$apkPath nohup app_process / com.jerry.oliver.server.Main >/data/local/tmp/oliver.log 2>&1 &"
}
