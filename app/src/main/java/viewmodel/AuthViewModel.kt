package viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import data.model.User
import data.repository.AuthRepository

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    val userLiveData = MutableLiveData<User>()
    val errorLiveData = MutableLiveData<String>()
    val registerSuccesLiveData = MutableLiveData<Boolean>()

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
}
