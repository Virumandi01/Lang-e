package com.example.language

data class ChatMessage(
    val text: String,
    val isUser: Boolean // True = User (Green), False = AI (Grey)
)