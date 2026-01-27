package com.example.projectthuctap.ui.reminders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.projectthuctap.base.BaseFragment
import com.example.projectthuctap.data.model.Reminder
import com.example.projectthuctap.data.session.SessionManager
import com.example.projectthuctap.databinding.FragmentAddReminderBinding
import com.example.projectthuctap.viewmodel.ReminderViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class AddReminderFragment :
    BaseFragment<FragmentAddReminderBinding>() {

    private val viewModel: ReminderViewModel by viewModels()

    private var selectedTime: Long = 0L

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAddReminderBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.tvDateTime.setOnClickListener {
            showDateTimePicker { time ->
                selectedTime = time

                val format = SimpleDateFormat(
                    "dd/MM/yyyy HH:mm",
                    Locale.getDefault()
                )

                binding.tvDateTime.text =
                    format.format(Date(time))
            }
        }

        binding.btnSave.setOnClickListener {
            saveReminder()
        }

        binding.btnBack.setOnClickListener {
            popBack()
        }
    }

    private fun saveReminder() {

        val title = binding.etTitle.text.toString().trim()
        val desc = binding.etDescription.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()

        if (title.isEmpty() || selectedTime == 0L) {
            showToast("Vui lòng nhập đủ thông tin")
            return
        }

        val now = System.currentTimeMillis()

        val reminder = Reminder(
            id = UUID.randomUUID().toString(),
            title = title,
            description = desc,
            dueTime = selectedTime,
            email = email,
            userId = SessionManager.userId ?: "",
            paid = false,
            isSent = false,
            createdAt = now,
            updatedAt = now
        )

        viewModel.addReminder(reminder)

        showToast("Đã thêm nhắc nhở")
        popBack()
    }
}



