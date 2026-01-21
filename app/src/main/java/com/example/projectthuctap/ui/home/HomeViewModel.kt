package com.example.projectthuctap.ui.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.projectthuctap.data.repository.TransactionRepository
import com.example.projectthuctap.data.repository.UserRepository

class HomeViewModel : ViewModel() {

    private val repo = TransactionRepository()
    private val userRepo = UserRepository()

    val userName = MutableLiveData<String>()

    val totalBalance = MutableLiveData<Double>()
    val totalIncomeAll = MutableLiveData<Double>()
    val totalExpenseAll = MutableLiveData<Double>()

    val incomeMonth = MutableLiveData<Double>()
    val expenseMonth = MutableLiveData<Double>()
    val diffMonth = MutableLiveData<Double>()

    fun loadUser() {
        userRepo.getUserName {
            userName.value = it
        }
    }

    fun loadOverview() {
        repo.getCurrentBalance {
            totalBalance.value = it
        }

        repo.getAllSummary { income, expense ->
            totalIncomeAll.value = income
            totalExpenseAll.value = expense
        }
    }

    fun loadDashboard(month: Int, year: Int) {
        repo.getMonthlySummary(month, year) { income, expense ->
            incomeMonth.value = income
            expenseMonth.value = expense
            diffMonth.value = income - expense
        }
    }
}

