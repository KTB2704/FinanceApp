package com.example.projectthuctap.data.repository

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.example.projectthuctap.data.model.User
import com.example.projectthuctap.data.session.SessionManager

class AuthRepository {

    private val userRef: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("users")

    fun login(
        email: String?,
        password: String?,
        userLiveData: MutableLiveData<User>,
        errorLiveData: MutableLiveData<String>
    ) {
        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            errorLiveData.value = "Không được để trống"
            return
        }

        userRef.orderByChild("email")
            .equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        errorLiveData.value = "Email không tồn tại"
                        return
                    }

                    for (child in snapshot.children) {
                        val user = child.getValue(User::class.java)

                        if (user == null) {
                            errorLiveData.value = "Dữ liệu người dùng lỗi"
                            return
                        }

                        if (user.password == password) {
                            SessionManager.userId = user.id
                            userLiveData.value = user
                            return
                        } else {
                            errorLiveData.value = "Sai mật khẩu"
                            return
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    errorLiveData.value = error.message
                }
            })
    }

    fun register(
        name: String?,
        email: String?,
        password: String?,
        repassword: String?,
        successLiveData: MutableLiveData<Boolean>,
        errorLiveData: MutableLiveData<String>
    ) {

        if (name.isNullOrBlank()
            || email.isNullOrBlank()
            || password.isNullOrBlank()
            || repassword.isNullOrBlank()
        ) {
            errorLiveData.value = "Không được để trống"
            return
        }

        if (password.length < 6) {
            errorLiveData.value = "Mật khẩu tối thiểu 6 ký tự"
            return
        }

        if (password != repassword) {
            errorLiveData.value = "Mật khẩu nhập lại không khớp"
            return
        }

        userRef.orderByChild("email")
            .equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()) {
                        errorLiveData.value = "Email đã tồn tại"
                        return
                    }

                    val userId = "user_${System.currentTimeMillis()}"
                    val user = User(userId, name, email, password)

                    val userNode = userRef.child(userId)

                    userNode.setValue(user)
                        .addOnSuccessListener {

                            val profile = hashMapOf(
                                "currency" to "VND",
                                "monthly_budget" to 0
                            )

                            userNode.child("profile").setValue(profile)
                            userNode.child("transactions")
                                .setValue(HashMap<String, Any>())

                            successLiveData.value = true
                        }
                        .addOnFailureListener { e ->
                            errorLiveData.value = e.message
                        }
                }

                override fun onCancelled(error: DatabaseError) {
                    errorLiveData.value = error.message
                }
            })
    }

    fun logout(logoutLiveData: MutableLiveData<Boolean>) {
        SessionManager.logOut()

        logoutLiveData.value = true
    }


}
