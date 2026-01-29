package com.example.projectthuctap.data.model

import java.io.Serializable

data class Transaction(
    val id: String = "",
    val userId: String = "",
    val amount: Double = 0.0,
    val categoryId: String = "",
    val categoryName: String = "",
    val categoryIcon: String = "",
    val type: String = "",
    val note: String? = null,
    val timestamp: Long = 0L
) : Serializable
