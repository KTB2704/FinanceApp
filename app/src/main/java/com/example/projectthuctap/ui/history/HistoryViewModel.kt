package com.example.projectthuctap.ui.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.projectthuctap.data.model.Transaction
import com.example.projectthuctap.data.repository.TransactionRepository

class HistoryViewModel : ViewModel() {

    private val repo = TransactionRepository()

    val transactions = MutableLiveData<List<Transaction>>()
    val income = MutableLiveData<Double>()
    val expense = MutableLiveData<Double>()

    fun loadData() {
        repo.getAllTransactions {
            transactions.value = it
        }

        repo.getAllSummary { inc, exp ->
            income.value = inc
            expense.value = exp
        }
    }
}
