package com.example.projectthuctap.ui.chatbot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectthuctap.base.BaseFragment
import com.example.projectthuctap.databinding.FragmentChatBotBinding
import com.example.projectthuctap.ui.adapter.AdapterChatBot

class ChatBotFragment : BaseFragment<FragmentChatBotBinding>() {

    private val viewModel: ChatBotViewModel by viewModels()
    private lateinit var adapter: AdapterChatBot

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentChatBotBinding {
        return FragmentChatBotBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClick()
        observeData()
    }

    private fun setupRecyclerView() {
        adapter = AdapterChatBot()

        binding.rvChat.apply {
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true
            }
            adapter = this@ChatBotFragment.adapter
        }
    }

    private fun setupClick() {
        binding.btnSend.setOnClickListener {
            val text = binding.edtMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                viewModel.sendMessage(text)
                binding.edtMessage.text?.clear()
            }
        }

        binding.btnBack.setOnClickListener {
            popBack()
        }
    }

    private fun observeData() {
        viewModel.messages.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            if (list.isNotEmpty()) {
                binding.rvChat.scrollToPosition(list.size - 1)
            }
        }
    }
}

