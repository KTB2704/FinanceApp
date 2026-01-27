package com.example.projectthuctap.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.projectthuctap.R
import com.example.projectthuctap.data.model.Transaction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    private val list = mutableListOf<Transaction>()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(data: List<Transaction>) {
        list.clear()
        list.addAll(data)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgCate: ImageView = view.findViewById(R.id.imgCate)
        val tvName: TextView = view.findViewById(R.id.tvNameC)
        val tvNote: TextView = view.findViewById(R.id.tvNote)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction_history, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val context = holder.itemView.context

        holder.tvName.text = item.categoryName
        holder.tvNote.text = item.note.orEmpty()

        holder.tvDate.text = SimpleDateFormat(
            context.getString(R.string.date_time_format),
            Locale.getDefault()
        ).format(Date(item.timestamp))

        holder.tvAmount.text = context.getString(
            R.string.money_format,
            item.amount.toInt()
        )

        val colorRes = if (item.type == "income") {
            R.color.income_color
        } else {
            R.color.expense_color
        }

        holder.tvAmount.setTextColor(
            ContextCompat.getColor(context, colorRes)
        )

        val resId = context.resources.getIdentifier(
            item.categoryIcon,
            "drawable",
            context.packageName
        )

        if (resId != 0) {
            holder.imgCate.setImageResource(resId)
        }
    }
}
