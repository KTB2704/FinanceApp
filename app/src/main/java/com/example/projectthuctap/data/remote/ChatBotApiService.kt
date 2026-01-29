package com.example.projectthuctap.data.remote

import com.example.projectthuctap.data.model.chatbot.ChatBotRequest
import com.example.projectthuctap.data.model.chatbot.ChatBotResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatBotApiService {

    @POST("webhook/a35cd300-b0fe-43b4-906a-488dc32c452d")
    suspend fun sendMessage(
        @Body request: ChatBotRequest
    ): Response<ChatBotResponse>
}

