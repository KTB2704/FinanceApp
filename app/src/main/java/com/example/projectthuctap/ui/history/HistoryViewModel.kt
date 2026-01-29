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

    fun loadByMonth(month: Int, year: Int){
        repo.getTransactionsByMonth(month, year){
            list -> transactions.value = list

            var  inc = 0.0
            var  exp = 0.0

            list.forEach {
                if (it.type == "income") inc += it.amount
                else exp += it.amount
            }

            income.value = inc
            expense.value = exp
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        repo.deleteTransaction(
            transaction,
            onSuccess = { loadData() },
            onError = { }
        )
    }


}
