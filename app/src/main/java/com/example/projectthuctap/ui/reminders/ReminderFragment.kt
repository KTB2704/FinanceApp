package com.example.projectthuctap.ui.reminders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectthuctap.base.BaseFragment
import com.example.projectthuctap.data.model.Reminder
import com.example.projectthuctap.databinding.FragmentReminderBinding
import com.example.projectthuctap.ui.adapter.AdapterReminder
import com.example.projectthuctap.viewmodel.ReminderViewModel

class ReminderFragment :
    BaseFragment<FragmentReminderBinding>() {

    private val viewModel: ReminderViewModel by viewModels()
    private lateinit var adapter: AdapterReminder

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentReminderBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        adapter = AdapterReminder(
            onPaid = { viewModel.markAsPaid(it) },
            onDelete = { viewModel.deleteReminder(it) },
            onEdit = { openEdit(it) }
        )

        binding.rvReminder.layoutManager =
            LinearLayoutManager(requireContext())
        binding.rvReminder.adapter = adapter

        binding.fabAdd.setOnClickListener {
            navigate(AddReminderFragment())
        }

        viewModel.allReminders.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.loadReminders()
    }

    private fun openEdit(reminder: Reminder) {
        navigate(EditReminderFragment.newInstance(reminder))
    }
}

