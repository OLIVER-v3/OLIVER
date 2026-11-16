package com.jerry.oliver.server

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.InetSocketAddress
import java.net.Socket

/** App-side connection to the server. Never call on the main thread. */
class IpcClient {
    private var socket: Socket? = null

    fun connect(): Boolean = try {
        val s = Socket()
        s.connect(InetSocketAddress("127.0.0.1", IpcProtocol.PORT), 500)
        socket = s
        true
    } catch (e: Exception) {
        false
    }

    @Synchronized
    private fun send(line: String) {
        socket?.getOutputStream()?.apply { write("$line\n".toByteArray()); flush() }
    }

    fun down(id: Int, x: Float, y: Float) = send("d $id $x $y")
    fun move(id: Int, x: Float, y: Float) = send("m $id $x $y")
    fun up(id: Int) = send("u $id")

    fun ping(): Boolean = try {
        send(IpcProtocol.PING)
        BufferedReader(InputStreamReader(socket!!.getInputStream())).readLine() == IpcProtocol.OK
    } catch (e: Exception) {
        false
    }

    fun close() {
        runCatching { send("q") }
        runCatching { socket?.close() }
        socket = null
    }
}
