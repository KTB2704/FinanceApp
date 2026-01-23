package com.example.projectthuctap.viewmodel

import Category
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.projectthuctap.data.repository.CategoryRepository
import com.example.projectthuctap.data.repository.TransactionRepository
import kotlin.math.abs

class TransactionViewModel : ViewModel() {

    private val repo = TransactionRepository()
    private val categoryRepo = CategoryRepository()

    val allCategories = MutableLiveData<List<Category>>()
    val filteredCategories = MutableLiveData<List<Category>>()
    val selectedCategory = MutableLiveData<Category?>()

    val transactionType = MutableLiveData("expense")
    val message = MutableLiveData<String>()
    val success = MutableLiveData<Boolean>()

    private val _adjustPreview = MutableLiveData<Pair<Double, String>>()

    val adjustPreview: LiveData<Pair<Double, String>> = _adjustPreview

    private var currentBalance: Double = 0.0

    private val _totalBalance = MutableLiveData<Double>()
    val totalBalance: LiveData<Double> = _totalBalance


    fun loadCategories() {
        categoryRepo.loadCategories(
            onSuccess = {
                allCategories.value = it
                filterCategory()
            },
            onError = { message.value = it }
        )
    }

    fun setTransactionType(type: String) {
        transactionType.value = type
        selectedCategory.value = null
        filterCategory()
    }

    fun selectCategory(category: Category) {
        selectedCategory.value = category
    }

    private fun filterCategory() {
        val type = transactionType.value ?: return
        val list = allCategories.value ?: return
        filteredCategories.value = list.filter { it.type == type }
    }

    private fun filterCategories(type: String) {
        val list = allCategories.value ?: return
        filteredCategories.value = list.filter { it.type == type }
    }


    fun saveTransaction(
        amount: String,
        note: String,
        time: Long
    ) {
        repo.saveTransaction(
            amountStr = amount,
            note = note,
            time = time,
            type = transactionType.value ?: "expense",
            category = selectedCategory.value,
            onSuccess = {
                success.value = true
                message.value = "Đã lưu giao dịch"
            },
            onError = { message.value = it }
        )

    }

    fun setCurrentBalance(balance: Double) {
        currentBalance = balance
    }

    fun loadCurrentBalance() {
        repo.getCurrentBalance { balance ->
            currentBalance = balance
            _totalBalance.value = balance
        }
    }

    fun calculateAdjust(realBalanceStr: String) {

        val realBalance = realBalanceStr
            .replace("đ", "")
            .replace(".", "")
            .replace(",", "")
            .trim()
            .toDoubleOrNull() ?: return

        val diff = realBalance - currentBalance

        if (diff == 0.0) {
            _adjustPreview.value = 0.0 to "none"
            return
        }

        val type = if (diff > 0) "income" else "expense"

        _adjustPreview.value = kotlin.math.abs(diff) to type
    }




}
