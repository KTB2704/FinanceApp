package com.example.projectthuctap.data.repository

import Category
import com.example.projectthuctap.data.model.Transaction
import com.example.projectthuctap.data.session.SessionManager
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class TransactionRepository {

    fun saveTransaction(
        amountStr: String,
        note: String,
        time: Long,
        type: String,
        category: Category?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        val userId = SessionManager.userId ?: run {
            onError("Chưa đăng nhập")
            return
        }

        val amount = amountStr.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            onError("Số tiền không hợp lệ")
            return
        }

        if (category == null) {
            onError("Chưa chọn hạng mục")
            return
        }

        val db = FirebaseDatabase.getInstance()
        val budgetRef = db.getReference("users/$userId/profile/budget")
        val transactionRef = db.getReference("transactions/$userId")
        val id = UUID.randomUUID().toString()

        budgetRef.get().addOnSuccessListener { snap ->

            val current = snap.getValue(Double::class.java) ?: 0.0
            val newBudget = if (type == "expense") current - amount else current + amount

            if (newBudget < 0) {
                onError("Ngân sách không đủ")
                return@addOnSuccessListener
            }

            val transaction = Transaction(
                id = id,
                userId = userId,
                amount = amount,
                categoryId = category.id,
                categoryName = category.name,
                categoryIcon = category.icon,
                type = type,
                note = note,
                timestamp = time
            )

            val updates = hashMapOf<String, Any>(
                "/users/$userId/profile/budget" to newBudget,
                "/transactions/$userId/$id" to transaction
            )

            db.reference.updateChildren(updates)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onError("Lưu giao dịch thất bại") }
        }
    }
}
