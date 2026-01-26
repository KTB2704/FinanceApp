package com.example.projectthuctap.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.projectthuctap.AuthActivity
import com.example.projectthuctap.databinding.FragmentProfileBinding
import com.example.projectthuctap.ui.auth.AuthViewModel
import com.example.projectthuctap.ui.home.HomeViewModel

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        observeViewModel()

        viewModel.loadUser()

        binding.btnLogOut.setOnClickListener {
            authViewModel.logout()
        }

        authViewModel.logoutSuccessLiveData.observe(viewLifecycleOwner) {
            if (it == true) {
                Toast.makeText(requireContext(), "Đã đăng xuất", Toast.LENGTH_SHORT).show()
                goToLogin()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.userName.observe(viewLifecycleOwner) {
            binding.txtName.text = it
        }
    }

    private fun goToLogin() {
        val intent = Intent(requireContext(), AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
