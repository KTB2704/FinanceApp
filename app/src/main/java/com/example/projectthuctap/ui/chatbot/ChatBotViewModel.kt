package com.example.projectthuctap.ui.chatbot

import androidx.lifecycle.*
import com.example.projectthuctap.data.model.ChatBotMessage
import com.example.projectthuctap.data.repository.ChatBotRepository
import com.example.projectthuctap.data.repository.TransactionRepository
import kotlinx.coroutines.launch

class ChatBotViewModel : ViewModel() {

    private val repository = ChatBotRepository()
    private val transactionRepository = TransactionRepository()

    private val _messages = MutableLiveData<List<ChatBotMessage>>(emptyList())
    val messages: LiveData<List<ChatBotMessage>> = _messages

    fun sendMessage(text: String) {

        // Thêm message user
        val updatedList = _messages.value!!.toMutableList()
        updatedList.add(ChatBotMessage(text, true))
        _messages.value = updatedList

        transactionRepository.getTransactionsFirebase(
            onSuccess = { transactions ->

                viewModelScope.launch {

                    val answer = repository.sendMessage(text, transactions)

                    val botList = _messages.value!!.toMutableList()
                    botList.add(ChatBotMessage(answer, false))

                    _messages.postValue(botList)
                }
            },
            onError = {
                val botList = _messages.value!!.toMutableList()
                botList.add(ChatBotMessage("Không lấy được dữ liệu", false))
                _messages.postValue(botList)
            }
        )
    }
}

