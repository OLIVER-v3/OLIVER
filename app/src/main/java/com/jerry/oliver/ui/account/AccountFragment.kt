package com.jerry.oliver.ui.account

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class AccountFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View =
        TextView(requireContext()).apply {
            text = "Account\n\nComing soon"
            gravity = Gravity.CENTER
            textSize = 18f
            setTextColor(Color.LTGRAY)
        }
}
