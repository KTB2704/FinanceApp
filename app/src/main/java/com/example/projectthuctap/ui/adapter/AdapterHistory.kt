package com.example.projectthuctap.ui.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projectthuctap.R
import com.example.projectthuctap.data.model.Transaction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter(
    private val context: Context
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    private val list = mutableListOf<Transaction>()

    fun submitList(data: List<Transaction>) {
        list.clear()
        list.addAll(data)
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgCate: ImageView = view.findViewById(R.id.imgCate)
        val tvName: TextView = view.findViewById(R.id.tvNameC)
        val tvNote: TextView = view.findViewById(R.id.tvNote)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_transaction_history, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val t = list[position]

        holder.tvName.text = t.categoryName
        holder.tvNote.text = t.note
        holder.tvDate.text = formatDateTime(t.timestamp)
        holder.tvAmount.text = "${t.amount.toInt()}đ"

        holder.tvAmount.setTextColor(
            if (t.type == "income")
                Color.parseColor("#2E7D32")
            else
                Color.parseColor("#E53935")
        )

        val resId = context.resources.getIdentifier(
            t.categoryIcon,
            "drawable",
            context.packageName
        )
        if (resId != 0) {
            holder.imgCate.setImageResource(resId)
        }
    }

    private fun formatDateTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
