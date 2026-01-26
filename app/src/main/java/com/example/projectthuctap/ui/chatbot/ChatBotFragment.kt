package com.example.projectthuctap.ui.chatbot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectthuctap.databinding.FragmentChatBotBinding
import com.example.projectthuctap.ui.adapter.AdapterChatBot

class ChatBotFragment : Fragment() {

    private var _binding: FragmentChatBotBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ChatBotViewModel
    private lateinit var adapter: AdapterChatBot

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBotBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[ChatBotViewModel::class.java]

        setupRecyclerView()
        setupClick()
        observeData()
    }

    private fun setupRecyclerView() {
        adapter = AdapterChatBot()

        binding.rvChat.layoutManager =
            LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true
            }

        binding.rvChat.adapter = adapter
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
            parentFragmentManager.popBackStack()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
