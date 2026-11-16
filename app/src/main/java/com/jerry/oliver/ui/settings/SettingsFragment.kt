package com.jerry.oliver.ui.settings

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.jerry.oliver.R
import com.jerry.oliver.activation.ActivationActivity
import com.jerry.oliver.util.dp

class SettingsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View {
        val ctx = requireContext()
        val logo = ImageView(ctx).apply { setImageResource(R.drawable.oliver_logo) }
        val name = TextView(ctx).apply {
            text = "OLIVER"
            textSize = 28f
            setTextColor(Color.parseColor("#FF1A00"))
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            gravity = Gravity.CENTER
        }
        val version = TextView(ctx).apply {
            text = "Keyboard & Mouse Mapper · v0.2.0"
            setTextColor(Color.GRAY)
            gravity = Gravity.CENTER
        }
        val activation = MaterialButton(ctx).apply {
            text = "Activation (Termux / ADB)"
            setOnClickListener { startActivity(Intent(ctx, ActivationActivity::class.java)) }
        }
        return LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(ctx.dp(24), ctx.dp(24), ctx.dp(24), ctx.dp(24))
            addView(logo, LinearLayout.LayoutParams(ctx.dp(140), ctx.dp(140)))
            addView(name, LinearLayout.LayoutParams(-1, -2))
            addView(version, LinearLayout.LayoutParams(-1, -2))
            addView(activation, LinearLayout.LayoutParams(-1, ctx.dp(56)).apply { topMargin = ctx.dp(32) })
        }
    }
}
