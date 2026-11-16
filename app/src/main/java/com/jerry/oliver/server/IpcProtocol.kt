package com.jerry.oliver.server

/**
 * Line protocol between the app and the shell-privileged server (localhost TCP).
 *   p                 -> server answers "ok"
 *   d <id> <x> <y>    finger down
 *   m <id> <x> <y>    finger move
 *   u <id>            finger up
 *   q                 close connection
 */
object IpcProtocol {
    const val PORT = 38417
    const val PING = "p"
    const val OK = "ok"
}
