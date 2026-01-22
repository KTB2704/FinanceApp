package com.example.projectthuctap.data.repository

import android.util.Log
import com.example.projectthuctap.data.model.ChatBotRequest
import com.example.projectthuctap.data.model.ChatBotResponse
import com.example.projectthuctap.data.model.Transaction
import com.example.projectthuctap.data.remote.RetrofitClient
import com.example.projectthuctap.data.session.SessionManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody

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

                // 🔥 Đọc RAW JSON từ server
                val rawBody: ResponseBody? = response.body() as? ResponseBody
                val raw = rawBody?.string()

                Log.e("API_DEBUG", "RAW RESPONSE: $raw")

                if (raw.isNullOrEmpty()) {
                    return "Server trả về rỗng"
                }

                val gson = Gson()

                return try {

                    // 🔥 Thử parse dạng List trước
                    val listType = object : TypeToken<List<ChatBotResponse>>() {}.type
                    val list: List<ChatBotResponse> = gson.fromJson(raw, listType)

                    list.firstOrNull()?.reply ?: "Bot không trả lời"

                } catch (e: Exception) {

                    Log.e("API_DEBUG", "Parse List lỗi -> thử parse Object")

                    try {
                        // 🔥 Nếu không phải List thì parse Object
                        val obj = gson.fromJson(raw, ChatBotResponse::class.java)
                        obj.reply ?: "Bot không trả lời"
                    } catch (ex: Exception) {
                        Log.e("API_DEBUG", "Parse Object cũng lỗi")
                        "Không parse được JSON"
                    }
                }

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
