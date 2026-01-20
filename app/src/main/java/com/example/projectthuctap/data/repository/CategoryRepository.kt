package com.example.projectthuctap.data.repository

import Category
import com.google.firebase.database.FirebaseDatabase

class CategoryRepository {

    fun loadCategories(
        onSuccess: (List<Category>) -> Unit,
        onError: (String) -> Unit
    ) {
        FirebaseDatabase.getInstance()
            .getReference("categories")
            .get()
            .addOnSuccessListener { snapshot ->
                val list = mutableListOf<Category>()
                for (child in snapshot.children) {
                    val category = child.getValue(Category::class.java)
                    category?.id = child.key ?: ""
                    category?.let { list.add(it) }
                }
                onSuccess(list)
            }
            .addOnFailureListener {
                onError("Không tải được hạng mục")
            }
    }
}
