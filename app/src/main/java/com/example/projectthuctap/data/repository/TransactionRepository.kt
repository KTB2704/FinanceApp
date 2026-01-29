package com.example.projectthuctap.data.repository

import Category
import android.util.Log
import com.example.projectthuctap.data.model.Transaction
import com.example.projectthuctap.data.session.SessionManager
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar
import java.util.UUID

class TransactionRepository {

    private val db = FirebaseDatabase.getInstance()

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
        if (amount == null) {
            onError("Yêu cầu nhập số tiền")
            return
        }

        if (amount <= 0) {
            onError("Số tiền phải lớn hơn 0")
            return
        }

        if (category == null) {
            onError("Chưa chọn hạng mục")
            return
        }

        val budgetRef = db.getReference("users/$userId/profile/budget")
        val id = UUID.randomUUID().toString()

        budgetRef.get().addOnSuccessListener { snap ->
            val current = snap.getValue(Double::class.java) ?: 0.0


            val newBudget =
                if (type == "expense") current - amount else current + amount

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

    fun getAllTransactions(
        onResult: (List<Transaction>) -> Unit
    ) {
        val userId = SessionManager.userId ?: return

        db.getReference("transactions/$userId")
            .get()
            .addOnSuccessListener { snap ->
                val list = mutableListOf<Transaction>()
                snap.children.forEach {
                    val t = it.getValue(Transaction::class.java)
                    t?.let(list::add)
                }
                onResult(list.sortedByDescending { it.timestamp })
            }
            .addOnFailureListener {
                Log.e("TRANS_REPO", "Load all failed", it)
            }
    }

    fun getTransactionsFirebase(
        onSuccess: (List<Transaction>) -> Unit,
        onError: (String) -> Unit
    ) {

        val userId = SessionManager.userId ?: run {
            onError("Chưa đăng nhập")
            return
        }

        db.getReference("transactions")
            .child(userId)
            .get()
            .addOnSuccessListener { snapshot ->

                val list = mutableListOf<Transaction>()

                snapshot.children.forEach {
                    val transaction = it.getValue(Transaction::class.java)
                    transaction?.let { t -> list.add(t) }
                }

                onSuccess(list)
            }
            .addOnFailureListener {
                onError("Lỗi tải dữ liệu")
            }
    }



    fun getCurrentBalance(onResult: (Double) -> Unit) {
        val userId = SessionManager.userId ?: return

        db.getReference("users/$userId/profile/budget")
            .get()
            .addOnSuccessListener {
                onResult(it.getValue(Double::class.java) ?: 0.0)
            }
    }

    fun getTransactionsByMonth(
        month: Int,
        year: Int,
        onResult: (List<Transaction>) -> Unit
    ) {
        val userId = SessionManager.userId ?: run {
            return
        }

        val start = Calendar.getInstance().apply {
            clear()
            set(year, month, 1, 0, 0, 0)
        }.timeInMillis

        val end = Calendar.getInstance().apply {
            clear()
            set(year, month + 1, 1, 0, 0, 0)
        }.timeInMillis - 1


        db.getReference("transactions/$userId")
            .orderByChild("timestamp")
            .startAt(start.toDouble())
            .endAt(end.toDouble())
            .get()
            .addOnSuccessListener { snap ->
                val list = mutableListOf<Transaction>()
                snap.children.forEach {
                    val t = it.getValue(Transaction::class.java)
                    t?.let(list::add)
                }
                onResult(list)
            }
            .addOnFailureListener {
                Log.e("DASH_REPO", "Firebase error", it)
            }
    }


    fun getMonthlySummary(
        month: Int,
        year: Int,
        onResult: (income: Double, expense: Double) -> Unit
    ) {
        getTransactionsByMonth(month, year) { list ->
            var income = 0.0
            var expense = 0.0

            list.forEach {
                when (it.type) {
                    "income" -> income += it.amount
                    "expense" -> expense += it.amount
                }
            }

            onResult(income, expense)
        }
    }

    fun getAllSummary(
        onResult: (income: Double, expense: Double) -> Unit
    ) {
        val userId = SessionManager.userId ?: return

        db.getReference("transactions/$userId")
            .get()
            .addOnSuccessListener { snap ->
                var income = 0.0
                var expense = 0.0

                snap.children.forEach {
                    val transaction = it.getValue(Transaction::class.java)
                    if (transaction != null) {
                        when (transaction.type) {
                            "income" -> income += transaction.amount
                            "expense" -> expense += transaction.amount
                        }
                    }
                }

                onResult(income, expense)
            }
    }

    fun deleteTransaction(
        transaction: Transaction,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val userId = SessionManager.userId ?: run {
            onError("Chưa đăng nhập")
            return
        }

        val budgetRef = db.getReference("users/$userId/profile/budget")

        budgetRef.get().addOnSuccessListener { snap ->
            val currentBudget = snap.getValue(Double::class.java) ?: 0.0

            val newBudget =
                if (transaction.type == "expense")
                    currentBudget + transaction.amount
                else
                    currentBudget - transaction.amount

            val updates = hashMapOf<String, Any?>(
                "/users/$userId/profile/budget" to newBudget,
                "/transactions/$userId/${transaction.id}" to null
            )

            db.reference.updateChildren(updates)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onError("Xoá thất bại") }
        }
    }


    fun updateTransaction(
        oldTransaction: Transaction,
        newAmountStr: String,
        newNote: String,
        newTime: Long,
        newType: String,
        newCategory: Category?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val userId = SessionManager.userId ?: run {
            onError("Chưa đăng nhập")
            return
        }

        val newAmount = newAmountStr.toDoubleOrNull()
        if (newAmount == null || newAmount <= 0) {
            onError("Số tiền không hợp lệ")
            return
        }

        if (newCategory == null) {
            onError("Chưa chọn hạng mục")
            return
        }

        val budgetRef = db.getReference("users/$userId/profile/budget")

        budgetRef.get().addOnSuccessListener { snap ->
            val currentBudget = snap.getValue(Double::class.java) ?: 0.0

            val revertBudget =
                if (oldTransaction.type == "expense")
                    currentBudget + oldTransaction.amount
                else
                    currentBudget - oldTransaction.amount

            val finalBudget =
                if (newType == "expense")
                    revertBudget - newAmount
                else
                    revertBudget + newAmount

            if (finalBudget < 0) {
                onError("Ngân sách không đủ")
                return@addOnSuccessListener
            }

            val updatedTransaction = oldTransaction.copy(
                amount = newAmount,
                note = newNote,
                timestamp = newTime,
                type = newType,
                categoryId = newCategory.id,
                categoryName = newCategory.name,
                categoryIcon = newCategory.icon
            )

            val updates = hashMapOf<String, Any>(
                "/users/$userId/profile/budget" to finalBudget,
                "/transactions/$userId/${oldTransaction.id}" to updatedTransaction
            )

            db.reference.updateChildren(updates)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onError("Cập nhật thất bại") }
        }
    }






}
