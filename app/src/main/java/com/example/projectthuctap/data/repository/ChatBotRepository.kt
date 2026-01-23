package com.example.projectthuctap.data.repository

import android.util.Log
import com.example.projectthuctap.data.model.chatbot.ChatBotRequest
import com.example.projectthuctap.data.model.Transaction
import com.example.projectthuctap.data.remote.RetrofitClient
import com.example.projectthuctap.data.session.SessionManager

class ChatBotRepository {

    suspend fun sendMessage(
        message: String,
        transactions: List<Transaction>
    ): String {

        val userId = SessionManager.userId ?: return "Chưa đăng nhập"

        return try {

            val request = ChatBotRequest(
                message = message,
                userId = userId,
                transactions = transactions
            )

            val response = RetrofitClient.api.sendMessage(request)

            Log.d("API_DEBUG", "HTTP Code: ${response.code()}")

            if (response.isSuccessful) {

                val body = response.body()

                Log.d("API_DEBUG", "Parsed body: $body")

                body?.reply ?: "Bot không trả lời"

            } else {

                val error = response.errorBody()?.string()
                Log.e("API_DEBUG", "Error body: $error")

                "Lỗi server: ${response.code()}"
            }

        } catch (e: Exception) {

            Log.e("API_DEBUG", "Exception: ${e.message}", e)

            "Lỗi kết nối server"
        }
    }
}
