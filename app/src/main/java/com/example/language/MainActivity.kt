package com.example.language // Ensure this matches your actual package name

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // 1. Fix the Window Insets (Padding for status/navigation bars)
        // Note: Change R.id.main to R.id.fragment_container or your root layout ID
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fragment_container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0) // Keep bottom at 0 for Nav bar
            insets
        }

        // 2. Initialize the Bottom Navigation View
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // 3. Set the Click Listener for the menu items
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dictionary -> {
                    loadFragment(DictionaryFragment())
                    true
                }
                R.id.nav_ai -> {
                    loadFragment(AiFragment())
                    true
                }
                R.id.nav_settings -> {
                    loadFragment(SettingsFragment())
                    true
                }
                else -> false
            }
        }

        // 4. Load the default fragment (Dictionary) when app first opens
        if (savedInstanceState == null) {
            loadFragment(DictionaryFragment())
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}