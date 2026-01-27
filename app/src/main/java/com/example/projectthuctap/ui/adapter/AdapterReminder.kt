package com.example.projectthuctap.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectthuctap.data.model.Reminder
import com.example.projectthuctap.databinding.ItemReminderBinding
import java.text.SimpleDateFormat
import java.util.*

class AdapterReminder(
    private val onEdit: (Reminder) -> Unit,
    private val onPaid: (Reminder) -> Unit,
    private val onDelete: (Reminder) -> Unit
) : RecyclerView.Adapter<AdapterReminder.ReminderViewHolder>() {

    private var list: List<Reminder> = emptyList()

    fun submitList(newList: List<Reminder>) {
        list = newList.sortedBy { it.dueTime }
        notifyDataSetChanged()
    }

    class ReminderViewHolder(
        val binding: ItemReminderBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReminderViewHolder {

        val binding = ItemReminderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ReminderViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ReminderViewHolder,
        position: Int
    ) {

        val reminder = list[position]
        val now = System.currentTimeMillis()
        val b = holder.binding

        b.tvTitle.text = reminder.title
        b.tvDescription.text = reminder.description

        val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        b.tvDueDate.text = "Hạn: ${format.format(Date(reminder.dueTime))}"

        b.btnPaid.isEnabled = true
        b.btnPaid.alpha = 1f

        when {
            reminder.paid -> {
                b.tvStatus.text = "ĐÃ THANH TOÁN"
                b.tvStatus.setBackgroundColor(Color.parseColor("#2E7D32"))
                b.btnPaid.isEnabled = false
                b.btnPaid.alpha = 0.5f
            }

            reminder.dueTime < now -> {
                b.tvStatus.text = "QUÁ HẠN"
                b.tvStatus.setBackgroundColor(Color.parseColor("#E53935"))
            }

            else -> {
                b.tvStatus.text = "ĐẾN HẠN"
                b.tvStatus.setBackgroundColor(Color.parseColor("#1565C0"))
            }
        }

        b.btnEdit.setOnClickListener {
            onEdit(reminder)
        }

        b.btnDelete.setOnClickListener {
            onDelete(reminder)
        }

        b.btnPaid.setOnClickListener {
            if (!reminder.paid) {
                onPaid(reminder)
            }
        }
    }

    override fun getItemCount(): Int = list.size
}
