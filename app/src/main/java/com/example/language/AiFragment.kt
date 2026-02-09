package com.example.language

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class AiFragment : Fragment() {

    // 1. Create the list to hold chat messages
    private val messages = mutableListOf<ChatMessage>()

    // 2. Create the adapter
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ai, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 3. Setup the RecyclerView (The List)
        val rvChatHistory = view.findViewById<RecyclerView>(R.id.rv_chat_history)
        chatAdapter = ChatAdapter(messages, requireContext())
        rvChatHistory.adapter = chatAdapter
        rvChatHistory.layoutManager = LinearLayoutManager(context)

        // 4. Add a default "Welcome" message
        if (messages.isEmpty()) {
            addMessage("Hello! I am your AI coding assistant. Ask me anything about Python, Java, or C++.", isUser = false)
        }

        // 5. Setup the Send Button Logic
        val etMessageInput = view.findViewById<EditText>(R.id.et_message_input)
        val btnSend = view.findViewById<ImageButton>(R.id.btn_send)

        btnSend.setOnClickListener {
            val userText = etMessageInput.text.toString().trim()
            if (userText.isNotEmpty()) {
                // Show User Message
                addMessage(userText, isUser = true)
                etMessageInput.text.clear()

                // Send to AI
                performAiRequest(userText)
            }
        }
    }

    // Helper function to add a message to the screen
    private fun addMessage(text: String, isUser: Boolean) {
        messages.add(ChatMessage(text, isUser))
        chatAdapter.notifyItemInserted(messages.size - 1)
        view?.findViewById<RecyclerView>(R.id.rv_chat_history)?.smoothScrollToPosition(messages.size - 1)
    }

    // The Logic to talk to Gemini
    private fun performAiRequest(question: String) {
        // 1. Retrieve the Key from Settings
        val prefs = requireContext().getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
        val savedApiKey = prefs.getString("gemini_api_key", "")

        // 2. Safety Check: If no key is found, warn the user
        if (savedApiKey.isNullOrEmpty()) {
            addMessage("⚠️ Please set your API Key in Settings first!", isUser = false)
            return // Stop here
        }

        // 3. Show "Thinking..." message
        addMessage("Thinking...", isUser = false)

        // 4. Start Background Task (Coroutine)
        MainScope().launch {
            try {
                // Setup the Brain with the SAVED Key
                val generativeModel = GenerativeModel(
                    modelName = "gemini-3-flash-preview", // Using the latest stable fast model
                    apiKey = savedApiKey
                )

                // Ask the question
                val response = generativeModel.generateContent(question)

                // Remove "Thinking..." (the last message)
                if (messages.isNotEmpty()) {
                    messages.removeAt(messages.size - 1)
                    chatAdapter.notifyItemRemoved(messages.size)
                }

                // Add the Real Answer
                addMessage(response.text ?: "No response", isUser = false)

            } catch (e: Exception) {
                // If "Thinking..." is still there, remove it
                if (messages.isNotEmpty() && messages.last().text == "Thinking...") {
                    messages.removeAt(messages.size - 1)
                    chatAdapter.notifyItemRemoved(messages.size)
                }
                // Show Error
                addMessage("Error: ${e.localizedMessage}", isUser = false)
            }
        }
    }
}