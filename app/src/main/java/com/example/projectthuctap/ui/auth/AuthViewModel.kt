package com.example.projectthuctap.ui.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.projectthuctap.data.model.User
import com.example.projectthuctap.data.repository.AuthRepository

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    val userLiveData = MutableLiveData<User>()
    val errorLiveData = MutableLiveData<String>()
    val registerSuccesLiveData = MutableLiveData<Boolean>()

    val logoutSuccessLiveData = MutableLiveData<Boolean>()

    fun login(email: String, password: String) {
        repository.login(email, password, userLiveData, errorLiveData)
    }

    fun register(email: String, name: String, password: String, repassword: String) {
        repository.register(
            name,
            email,
            password,
            repassword,
            registerSuccesLiveData,
            errorLiveData
        )
    }

    fun logout() {
        repository.logout(logoutSuccessLiveData)
    }
}