package com.example.projectthuctap.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.projectthuctap.data.model.Reminder
import com.example.projectthuctap.data.repository.ReminderRepository

class ReminderViewModel : ViewModel() {

    private val repo = ReminderRepository()

    private val _allReminders = MutableLiveData<List<Reminder>>()
    val allReminders: LiveData<List<Reminder>> = _allReminders

    fun loadReminders() {
        repo.getReminders(_allReminders)
    }

    fun addReminder(reminder: Reminder) {
        repo.addReminder(reminder)
    }

    fun updateReminder(reminder: Reminder) {
        repo.updateReminder(reminder)
    }

    fun deleteReminder(reminder: Reminder) {
        repo.deleteReminder(reminder)
    }

    fun markAsPaid(reminder: Reminder) {
        repo.markAsPaid(reminder)
    }

}
