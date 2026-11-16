package com.jerry.oliver.activation

import android.graphics.Color
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.jerry.oliver.server.IpcClient
import com.jerry.oliver.util.dp

/** Explains how to activate OLIVER with Termux (Wireless debugging) and checks the server. */
class ActivationActivity : AppCompatActivity() {

    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val steps = TextView(this).apply {
            setTextColor(Color.WHITE)
            textSize = 14f
            text = """
                Activation with Termux (no PC needed)

                1. Settings > Developer options > turn on "Wireless debugging" (connect to Wi-Fi or hotspot).
                2. Tap "Pair device with pairing code" - note the pairing port and code.
                3. In Termux (first time only):
                   bash oliver-activate.sh pair <PAIR_PORT> <PAIR_CODE>
                4. Back on the Wireless debugging screen, note the connection port, then:
                   bash oliver-activate.sh start <CONNECT_PORT>
                5. Come back here and tap "Check server".

                The script is in the project folder: scripts/oliver-activate.sh
            """.trimIndent()
        }
        status = TextView(this).apply {
            setTextColor(Color.LTGRAY)
            text = "Server: unknown"
            setPadding(0, dp(16), 0, dp(8))
        }
        val check = MaterialButton(this).apply {
            text = "Check server"
            setOnClickListener { checkServer() }
        }

        setContentView(ScrollView(this).apply {
            addView(LinearLayout(this@ActivationActivity).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(dp(20), dp(20), dp(20), dp(20))
                addView(steps)
                addView(status)
                addView(check)
            })
        })
    }

    private fun checkServer() {
        status.text = "Server: checking..."
        Thread {
            val client = IpcClient()
            val ok = client.connect() && client.ping()
            client.close()
            runOnUiThread { status.text = if (ok) "Server: RUNNING ✓" else "Server: not running ✗" }
        }.start()
    }
}
