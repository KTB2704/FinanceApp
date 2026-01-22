package com.example.projectthuctap.ui.profile

import androidx.lifecycle.MutableLiveData
import com.example.projectthuctap.data.repository.UserRepository

class ProfileViewModel{
    private val userRepo = UserRepository()

    val userName = MutableLiveData<String>()

    fun loadUser() {
        userRepo.getUserName {
            userName.value = it
        }
    }

}