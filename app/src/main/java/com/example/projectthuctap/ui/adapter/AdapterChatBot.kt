package com.example.projectthuctap.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projectthuctap.R
import com.example.projectthuctap.data.model.chatbot.ChatBotMessage

class AdapterChatBot : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val messages = mutableListOf<ChatBotMessage>()

    companion object {
        private const val TYPE_USER = 1
        private const val TYPE_BOT = 2
    }

    fun submitList(list: List<ChatBotMessage>) {
        messages.clear()
        messages.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isUser) TYPE_USER else TYPE_BOT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        return if (viewType == TYPE_USER) {
            val view = inflater.inflate(R.layout.item_chat_user, parent, false)
            UserViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.item_chat_bot, parent, false)
            BotViewHolder(view)
        }
    }

    override fun getItemCount() = messages.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val message = messages[position]

        when (holder) {
            is UserViewHolder -> holder.bind(message)
            is BotViewHolder -> holder.bind(message)
        }
    }

    inner class UserViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        private val tvMessage: TextView = view.findViewById(R.id.tvMessage)

        fun bind(message: ChatBotMessage) {
            tvMessage.text = message.message
        }
    }

    inner class BotViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        private val tvMessage: TextView = view.findViewById(R.id.tvMessage)

        fun bind(message: ChatBotMessage) {
            tvMessage.text = message.message
        }
    }
}


