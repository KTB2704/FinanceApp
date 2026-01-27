package com.example.projectthuctap.ui.reminders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.projectthuctap.base.BaseFragment
import com.example.projectthuctap.data.model.Reminder
import com.example.projectthuctap.databinding.FragmentEditReminderBinding
import com.example.projectthuctap.viewmodel.ReminderViewModel
import java.text.SimpleDateFormat
import java.util.*

class EditReminderFragment :
    BaseFragment<FragmentEditReminderBinding>() {

    private val viewModel: ReminderViewModel by viewModels()

    private lateinit var reminder: Reminder
    private var selectedTime: Long = 0L

    companion object {
        private const val KEY_REMINDER = "reminder"

        fun newInstance(reminder: Reminder): EditReminderFragment {
            return EditReminderFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(KEY_REMINDER, reminder)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val data = arguments?.getSerializable(KEY_REMINDER)
            ?: run {
                popBack()
                return
            }

        reminder = data as Reminder
        selectedTime = reminder.dueTime
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentEditReminderBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val format =
            SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        binding.etTitle.setText(reminder.title)
        binding.etDescription.setText(reminder.description)
        binding.etEmail.setText(reminder.email)
        binding.tvDateTime.text =
            format.format(Date(reminder.dueTime))

        binding.cbPaid.isChecked = reminder.paid

        binding.tvDateTime.setOnClickListener {
            showDateTimePicker(selectedTime) { time ->
                selectedTime = time
                binding.tvDateTime.text =
                    format.format(Date(time))
            }
        }

        binding.btnSave.setOnClickListener {
            updateReminder()
        }

        binding.btnBack.setOnClickListener {
            popBack()
        }
    }

    private fun updateReminder() {

        val updated = reminder.copy(
            title = binding.etTitle.text.toString().trim(),
            description = binding.etDescription.text.toString().trim(),
            email = binding.etEmail.text.toString().trim(),
            dueTime = selectedTime,
            paid = binding.cbPaid.isChecked,
            updatedAt = System.currentTimeMillis()
        )

        viewModel.updateReminder(updated)

        showToast("Đã cập nhật")
        popBack()
    }
}


