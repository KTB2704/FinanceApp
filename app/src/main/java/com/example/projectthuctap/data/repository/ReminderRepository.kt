package com.example.projectthuctap.data.repository

import androidx.lifecycle.MutableLiveData
import com.example.projectthuctap.data.model.Reminder
import com.example.projectthuctap.data.session.SessionManager
import com.google.firebase.database.*

class ReminderRepository {

    private val db = FirebaseDatabase.getInstance().reference
    private val reminderRef = db.child("reminders")

    private fun getUserId(): String? {
        return SessionManager.userId
    }

    fun getReminders(liveData: MutableLiveData<List<Reminder>>) {

        val userId = getUserId() ?: return

        reminderRef.child(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val list = mutableListOf<Reminder>()

                    for (child in snapshot.children) {

                        val reminder = child.getValue(Reminder::class.java)

                        reminder?.let {
                            val reminderWithId = it.copy(
                                id = child.key ?: ""
                            )
                            list.add(reminderWithId)
                        }
                    }

                    liveData.value = list.sortedBy { it.dueTime }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun addReminder(reminder: Reminder) {

        val userId = getUserId() ?: return

        val key = reminderRef.child(userId).push().key ?: return

        val newReminder = reminder.copy(
            id = key,
            userId = userId,
            paid = false,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        reminderRef.child(userId)
            .child(key)
            .setValue(newReminder)
    }

    fun updateReminder(reminder: Reminder) {
        reminderRef.child(reminder.userId)
            .child(reminder.id)
            .setValue(reminder)
    }

    fun deleteReminder(reminder: Reminder) {
        reminderRef.child(reminder.userId)
            .child(reminder.id)
            .removeValue()
    }

    fun markAsPaid(reminder: Reminder) {

        val userId = getUserId() ?: return

        val updates = mapOf<String, Any>(
            "paid" to true,
            "updatedAt" to System.currentTimeMillis()
        )

        reminderRef.child(userId)
            .child(reminder.id)
            .updateChildren(updates)
    }

}
