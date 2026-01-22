package com.example.projectthuctap.ui.chatbot

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectthuctap.R
import com.example.projectthuctap.ui.adapter.AdapterChatBot

class ChatBotFragment : Fragment(R.layout.fragment_chat_bot) {

    private lateinit var viewModel: ChatBotViewModel
    private lateinit var adapter: AdapterChatBot

    private lateinit var rvChat: RecyclerView
    private lateinit var edtMessage: EditText
    private lateinit var btnSend: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindView(view)

        viewModel = ViewModelProvider(this)[ChatBotViewModel::class.java]

        adapter = AdapterChatBot()

        rvChat.layoutManager = LinearLayoutManager(requireContext()).apply {
            stackFromEnd = true
        }
        rvChat.adapter = adapter

        btnSend.setOnClickListener {

            val text = edtMessage.text.toString().trim()

            if (text.isNotEmpty()) {
                viewModel.sendMessage(text)
                edtMessage.text.clear()
            }
        }

        observeData()
    }

    private fun bindView(view: View) {
        rvChat = view.findViewById(R.id.rvChat)
        edtMessage = view.findViewById(R.id.edtMessage)
        btnSend = view.findViewById(R.id.btnSend)
    }

    private fun observeData() {

        viewModel.messages.observe(viewLifecycleOwner) { list ->

            adapter.submitList(list)

            if (list.isNotEmpty()) {
                view?.findViewById<RecyclerView>(R.id.rvChat)
                    ?.scrollToPosition(list.size - 1)
            }
        }
    }
}

