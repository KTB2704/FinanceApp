package com.example.projectthuctap.data.session

object SessionManager {
    var userId: String? = null

    fun isLoggedIn(): Boolean = userId != null

    fun logOut() {
        userId = null
    }
}
