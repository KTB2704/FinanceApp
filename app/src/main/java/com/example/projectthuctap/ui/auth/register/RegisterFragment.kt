package com.example.projectthuctap.ui.auth.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.projectthuctap.databinding.FragmentRegisterBinding
import com.example.projectthuctap.ui.auth.AuthViewModel

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        setupClick()
        observeViewModel()
    }

    private fun setupClick() {

        binding.btnRe.setOnClickListener {
            viewModel.register(
                binding.etEmail.text.toString().trim(),
                binding.etName.text.toString().trim(),
                binding.etPassword.text.toString().trim(),
                binding.etRePassword.text.toString().trim()
            )
        }

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun observeViewModel() {

        viewModel.registerSuccesLiveData.observe(viewLifecycleOwner) { success ->
            if (success == true) {
                Toast.makeText(
                    requireContext(),
                    "Đăng ký tài khoản thành công",
                    Toast.LENGTH_SHORT
                ).show()
                parentFragmentManager.popBackStack()
            }
        }

        viewModel.errorLiveData.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
