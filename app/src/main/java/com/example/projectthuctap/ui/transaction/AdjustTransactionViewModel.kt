package com.example.projectthuctap.viewmodel

import Category
import androidx.lifecycle.*
import com.example.projectthuctap.data.repository.CategoryRepository
import com.example.projectthuctap.data.repository.TransactionRepository
import kotlin.math.abs

class AdjustTransactionViewModel : ViewModel() {

    private val repo = TransactionRepository()
    private val categoryRepo = CategoryRepository()

    private val _allCategories = MutableLiveData<List<Category>>()
    val filteredCategories = MutableLiveData<List<Category>>()
    val selectedCategory = MutableLiveData<Category?>()

    private val _transactionType = MutableLiveData("expense")
    val transactionType: LiveData<String> = _transactionType

    private val _adjustPreview = MutableLiveData<Pair<Double, String>>()
    val adjustPreview: LiveData<Pair<Double, String>> = _adjustPreview

    private val _totalBalance = MutableLiveData<Double>()
    val totalBalance: LiveData<Double> = _totalBalance

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    private val _success = MutableLiveData<Boolean?>()
    val success: LiveData<Boolean?> = _success

    private var currentBalance = 0.0

    fun loadCategories() {
        categoryRepo.loadCategories(
            onSuccess = {
                _allCategories.value = it
                filterCategory()
            },
            onError = { _message.value = it }
        )
    }

    fun loadCurrentBalance() {
        repo.getCurrentBalance {
            currentBalance = it
            _totalBalance.value = it
        }
    }

    fun setTransactionType(type: String) {
        _transactionType.value = type
        selectedCategory.value = null
        filterCategory()
    }

    private fun filterCategory() {
        val type = _transactionType.value ?: return
        val list = _allCategories.value ?: return
        filteredCategories.value = list.filter { it.type == type }
    }

    fun selectCategory(category: Category) {
        selectedCategory.value = category
    }

    fun calculateAdjust(realBalanceStr: String) {

        val clean = realBalanceStr
            .replace("đ", "")
            .replace(".", "")
            .replace(",", "")
            .trim()

        if (clean.isEmpty()) {
            _adjustPreview.value = 0.0 to "none"
            return
        }

        val real = clean.toDoubleOrNull() ?: return
        val diff = real - currentBalance

        if (diff == 0.0) {
            _adjustPreview.value = 0.0 to "none"
            return
        }

        val type = if (diff > 0) "income" else "expense"

        _transactionType.value = type
        filterCategory()

        _adjustPreview.value = abs(diff) to type
    }

    fun saveTransaction(amount: Double, note: String, time: Long) {

        val category = selectedCategory.value
        val type = _transactionType.value ?: "expense"

        if (category == null) {
            _message.value = "Vui lòng chọn hạng mục"
            return
        }

        repo.saveTransaction(
            amountStr = amount.toString(),
            note = note,
            time = time,
            type = type,
            category = category,
            onSuccess = {
                _success.value = true
            },
            onError = { _message.value = it }
        )
    }

    fun resetState() {
        selectedCategory.value = null
        _transactionType.value = "expense"
        _success.value = null
        _message.value = null
        _adjustPreview.value = 0.0 to "none"
    }
}
