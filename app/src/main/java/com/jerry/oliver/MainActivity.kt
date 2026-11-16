package com.jerry.oliver

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jerry.oliver.editor.EditorActivity
import com.jerry.oliver.keymap.KeymapStore

class MainActivity : AppCompatActivity() {

    private lateinit var store: KeymapStore
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        store = KeymapStore(this)
        status = findViewById(R.id.txtStatus)

        findViewById<android.view.View>(R.id.btnEditor).setOnClickListener {
            startActivity(Intent(this, EditorActivity::class.java))
        }
        findViewById<android.view.View>(R.id.btnExport).setOnClickListener {
            val json = store.load(EditorActivity.PROFILE).toJsonString()
            val send = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, json)
            }
            startActivity(Intent.createChooser(send, "Export profile"))
        }
    }

    override fun onResume() {
        super.onResume()
        val p = store.load(EditorActivity.PROFILE)
        status.text = "Profile: ${p.name} · ${p.buttons.size} buttons"
    }
}
