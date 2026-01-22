package com.example.projectthuctap.data.model

data class ChatBotMessage(
    val message: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)


