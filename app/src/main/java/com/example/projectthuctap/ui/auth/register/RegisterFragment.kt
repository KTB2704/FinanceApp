package com.example.projectthuctap.ui.auth.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.projectthuctap.R
import com.example.projectthuctap.ui.auth.AuthViewModel

class RegisterFragment : Fragment() {

    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_register, container, false)

        val btnBack = view.findViewById<ImageView>(R.id.btnBack)
        val etEmail = view.findViewById<EditText>(R.id.etEmail)
        val etName = view.findViewById<EditText>(R.id.etName)
        val etPassword = view.findViewById<EditText>(R.id.etPassword)
        val etRePassword = view.findViewById<EditText>(R.id.etRePassword)
        val btnRe = view.findViewById<Button>(R.id.btnRe)

        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        btnRe.setOnClickListener {
            viewModel.register(
                etEmail.text.toString().trim(),
                etName.text.toString().trim(),
                etPassword.text.toString().trim(),
                etRePassword.text.toString().trim()
            )
        }

        viewModel.registerSuccesLiveData.observe(viewLifecycleOwner) { success ->
            if (success == true) {
                Toast.makeText(
                    requireContext(),
                    "Đăng ký tài khoản thành công",
                    Toast.LENGTH_SHORT
                ).show()
                requireActivity().supportFragmentManager.popBackStack()
            }
        }

        viewModel.errorLiveData.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        return view
    }
}