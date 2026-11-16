package com.jerry.oliver

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jerry.oliver.ui.account.AccountFragment
import com.jerry.oliver.ui.games.GamesFragment
import com.jerry.oliver.ui.settings.SettingsFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val nav = findViewById<BottomNavigationView>(R.id.bottomNav)
        nav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_games -> show(GamesFragment())
                R.id.nav_account -> show(AccountFragment())
                R.id.nav_settings -> show(SettingsFragment())
            }
            true
        }
        if (savedInstanceState == null) nav.selectedItemId = R.id.nav_games
    }

    private fun show(f: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.container, f).commit()
    }
}
