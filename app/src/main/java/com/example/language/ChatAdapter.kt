package com.example.language

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.noties.markwon.Markwon
import io.noties.markwon.linkify.LinkifyPlugin

class ChatAdapter(private val messages: List<ChatMessage>, context: Context) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    // 1. Initialize the Markdown Processor
    private val markwon: Markwon = Markwon.builder(context)
        .usePlugin(LinkifyPlugin.create()) // Makes links clickable
        // (Optional) We will add Syntax Highlighting in the next step
        .build()

    class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val aiText: TextView = view.findViewById(R.id.tv_ai_message)
        val userText: TextView = view.findViewById(R.id.tv_user_message)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_message, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messages[position]

        if (message.isUser) {
            // User Message: Just plain text is fine
            holder.userText.text = message.text
            holder.userText.visibility = View.VISIBLE
            holder.aiText.visibility = View.GONE
        } else {
            // AI Message: Render Markdown!
            markwon.setMarkdown(holder.aiText, message.text)

            holder.aiText.visibility = View.VISIBLE
            holder.userText.visibility = View.GONE
        }
    }

    override fun getItemCount() = messages.size
}