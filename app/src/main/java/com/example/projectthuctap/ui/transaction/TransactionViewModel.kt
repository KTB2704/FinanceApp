package com.example.projectthuctap.viewmodel

import Category
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.projectthuctap.data.model.Transaction
import com.example.projectthuctap.data.repository.CategoryRepository
import com.example.projectthuctap.data.repository.TransactionRepository

class TransactionViewModel : ViewModel() {

    private val repo = TransactionRepository()
    private val categoryRepo = CategoryRepository()

    val allCategories = MutableLiveData<List<Category>>()
    val filteredCategories = MutableLiveData<List<Category>>()
    val selectedCategory = MutableLiveData<Category?>()

    val transactionType = MutableLiveData("expense")
    val message = MutableLiveData<String>()
    val success = MutableLiveData<Boolean>()

    fun loadCategories() {
        categoryRepo.loadCategories(
            onSuccess = {
                allCategories.value = it
                filterCategory()
            },
            onError = { message.value = it }
        )
    }

    fun setTransactionType(type: String, isEdit: Boolean = false) {
        transactionType.value = type

        if (!isEdit) {
            selectedCategory.value = null
        }

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

    fun saveTransaction(
        amount: String,
        note: String,
        time: Long
    ) {

        val category = selectedCategory.value

        if (category == null) {
            message.value = "Vui lòng chọn hạng mục"
            return
        }

        repo.saveTransaction(
            amountStr = amount,
            note = note,
            time = time,
            type = transactionType.value ?: "expense",
            category = category,
            onSuccess = {
                success.value = true
                message.value = "Đã lưu giao dịch"
            },
            onError = { message.value = it }
        )
    }

    fun updateTransaction(
        oldTransaction: Transaction,
        amount: String,
        note: String,
        time: Long
    ) {

        val category = selectedCategory.value ?: Category(
            id = oldTransaction.categoryId,
            name = oldTransaction.categoryName,
            icon = oldTransaction.categoryIcon,
            type = oldTransaction.type
        )

        repo.updateTransaction(
            oldTransaction = oldTransaction,
            newAmountStr = amount,
            newNote = note,
            newTime = time,
            newType = transactionType.value ?: oldTransaction.type,
            newCategory = category,
            onSuccess = {
                success.value = true
                message.value = "Cập nhật thành công"
            },
            onError = {
                message.value = it
            }
        )
    }
}
