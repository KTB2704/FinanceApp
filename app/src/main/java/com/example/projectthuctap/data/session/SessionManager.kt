package com.example.projectthuctap.data.session

object SessionManager {
    var userId: String? = null

    fun logOut() {
        userId = null
    }
}
