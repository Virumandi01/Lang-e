package com.example.language

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.core.content.edit

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Get references to UI elements
        val etApiKey = view.findViewById<EditText>(R.id.et_api_key)
        val btnSaveKey = view.findViewById<Button>(R.id.btn_save_key)
        val btnClearHistory = view.findViewById<Button>(R.id.btn_clear_history)

        // 2. Open the "Secret Box" (SharedPreferences)
        // We use "app_prefs" as the file name to store our settings
        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        // 3. LOAD existing key (if user already saved one)
        val savedKey = sharedPreferences.getString("gemini_api_key", "")
        if (!savedKey.isNullOrEmpty()) {
            etApiKey.setText(savedKey)
        }

        // 4. SAVE Logic
        btnSaveKey.setOnClickListener {
            val newKey = etApiKey.text.toString().trim()

            if (newKey.isNotEmpty()) {
                // THE FIX: Use the clean "edit {}" block
                sharedPreferences.edit {
                    putString("gemini_api_key", newKey)
                }
                // Note: We don't need .apply() anymore, the {} block does it automatically!

                Toast.makeText(context, "API Key Saved!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Please enter a valid key", Toast.LENGTH_SHORT).show()
            }
        }

        // 5. Clear History Logic (Placeholder for now)
        btnClearHistory.setOnClickListener {
            Toast.makeText(context, "Chat History Cleared (Visual only for now)", Toast.LENGTH_SHORT).show()
            // Later we can connect this to the actual database
        }
    }
}