package com.example.projectthuctap.ui.auth.register
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.projectthuctap.base.BaseFragment
import com.example.projectthuctap.databinding.FragmentRegisterBinding
import com.example.projectthuctap.ui.auth.AuthViewModel

class RegisterFragment : BaseFragment<FragmentRegisterBinding>() {

    private val viewModel: AuthViewModel by activityViewModels()

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRegisterBinding {
        return FragmentRegisterBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            popBack()
        }
    }

    private fun observeViewModel() {

        viewModel.registerSuccesLiveData.observe(viewLifecycleOwner) { success ->
            if (success == true) {
                showToast("Đăng ký tài khoản thành công")
                popBack()
            }
        }

        viewModel.errorLiveData.observe(viewLifecycleOwner) {
            it?.let { showToast(it) }
        }
    }
}
