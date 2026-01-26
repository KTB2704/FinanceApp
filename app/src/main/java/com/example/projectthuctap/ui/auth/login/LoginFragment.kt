package com.example.projectthuctap.ui.auth.login
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.projectthuctap.MainActivity
import com.example.projectthuctap.R
import com.example.projectthuctap.base.BaseFragment
import com.example.projectthuctap.databinding.FragmentLoginBinding
import com.example.projectthuctap.ui.auth.AuthViewModel
import com.example.projectthuctap.ui.auth.register.RegisterFragment

class LoginFragment : BaseFragment<FragmentLoginBinding>() {

    private val viewModel: AuthViewModel by activityViewModels()

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLoginBinding {
        return FragmentLoginBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClick()
        observeViewModel()
    }

    private fun setupClick() {

        binding.btnRe.setOnClickListener {
            navigate(
                fragment = RegisterFragment(),
                containerId = R.id.auth_container
            )
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

        viewModel.errorLiveData.observe(viewLifecycleOwner) {
            showToast(it)
        }
    }
}

