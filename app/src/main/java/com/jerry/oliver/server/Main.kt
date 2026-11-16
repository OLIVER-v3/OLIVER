package com.jerry.oliver.server

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.InetAddress
import java.net.ServerSocket

/** Entry point launched with: CLASSPATH=<apk> app_process / com.jerry.oliver.server.Main */
object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val injector = TouchInjector()
        val server = ServerSocket(IpcProtocol.PORT, 1, InetAddress.getByName("127.0.0.1"))
        println("OLIVER server listening on ${IpcProtocol.PORT}")
        while (true) {
            server.accept().use { socket ->
                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                val out = socket.getOutputStream()
                while (true) {
                    val line = reader.readLine() ?: break
                    val p = line.trim().split(" ")
                    try {
                        when (p[0]) {
                            IpcProtocol.PING -> { out.write("${IpcProtocol.OK}\n".toByteArray()); out.flush() }
                            "d" -> injector.down(p[1].toInt(), p[2].toFloat(), p[3].toFloat())
                            "m" -> injector.move(p[1].toInt(), p[2].toFloat(), p[3].toFloat())
                            "u" -> injector.up(p[1].toInt())
                            "q" -> break
                        }
                    } catch (e: Exception) {
                        System.err.println("bad command: $line ($e)")
                    }
                }
            }
        }
    }
}
