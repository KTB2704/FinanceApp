package com.example.projectthuctap.data.model

import java.io.Serializable

data class Reminder(
    var id: String = "",
    var title: String = "",
    var description: String = "",
    var dueTime: Long = 0L,
    var email: String = "",
    var userId: String = "",
    var paid: Boolean = false,
    var isSent: Boolean = false,
    var createdAt: Long = 0L,
    var updatedAt: Long = 0L
): Serializable



