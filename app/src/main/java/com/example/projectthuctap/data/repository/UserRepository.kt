package com.example.projectthuctap.data.repository

import com.example.projectthuctap.data.session.SessionManager
import com.google.firebase.database.FirebaseDatabase

class UserRepository {

    private val db = FirebaseDatabase.getInstance()

    fun getUserName(onResult: (String) -> Unit) {
        val userId = SessionManager.userId ?: run {
            return
        }

        db.getReference("users/$userId/name")
            .get()
            .addOnSuccessListener {
                val name = it.getValue(String::class.java) ?: ""
                onResult(name)
            }
    }
}
