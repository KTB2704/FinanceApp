package com.example.projectthuctap.ui.adapter

import Category
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projectthuctap.R

class CategoryAdapter(
    private val context: Context,
    private val list: List<Category>,
    private val onClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgIcon: ImageView = view.findViewById(R.id.imgIcon)
        val tvName: TextView = view.findViewById(R.id.tvName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_category, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = list[position]
        holder.tvName.text = category.name

        val resId = context.resources.getIdentifier(
            category.icon,
            "drawable",
            context.packageName
        )

        if (resId != 0) {
            holder.imgIcon.setImageResource(resId)
        }

        holder.itemView.setOnClickListener {
            onClick(category)
        }
    }
}
