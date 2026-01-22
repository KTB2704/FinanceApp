package com.example.projectthuctap.data.model

import com.example.projectthuctap.data.model.Transaction

data class ChatBotRequest(
    val message: String,
    val userId: String,
    val transactions: List<Transaction>
)
