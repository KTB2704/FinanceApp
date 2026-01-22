package com.example.projectthuctap.data.remote

import com.example.projectthuctap.data.model.ChatBotRequest
import com.example.projectthuctap.data.model.ChatBotResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatBotApiService {

    @POST("webhook/67220706-a76e-4e78-bb28-682a80ea8210")
    suspend fun sendMessage(
        @Body request: ChatBotRequest
    ): Response<List<ChatBotResponse>>



}

