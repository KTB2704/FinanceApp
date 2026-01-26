package com.example.projectthuctap.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.projectthuctap.MainActivity
import com.example.projectthuctap.databinding.FragmentLoginBinding
import com.example.projectthuctap.ui.auth.AuthViewModel
import com.example.projectthuctap.ui.auth.register.RegisterFragment

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
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
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    com.example.projectthuctap.R.anim.slide_in_right,
                    com.example.projectthuctap.R.anim.slide_out_left,
                    com.example.projectthuctap.R.anim.slide_in_left,
                    com.example.projectthuctap.R.anim.slide_out_right
                )
                .replace(
                    com.example.projectthuctap.R.id.auth_container,
                    RegisterFragment()
                )
                .addToBackStack(null)
                .commit()
        }

        binding.btnLog.setOnClickListener {
            viewModel.login(
                binding.etEmail.text.toString().trim(),
                binding.etPassword.text.toString().trim()
            )
        }
    }

    private fun observeViewModel() {

        viewModel.userLiveData.observe(viewLifecycleOwner) {
            val intent = Intent(requireActivity(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        viewModel.errorLiveData.observe(viewLifecycleOwner) { msg ->
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
