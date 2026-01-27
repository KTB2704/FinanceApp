package com.example.projectthuctap.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.projectthuctap.AuthActivity
import com.example.projectthuctap.R
import com.example.projectthuctap.base.BaseFragment
import com.example.projectthuctap.databinding.FragmentProfileBinding
import com.example.projectthuctap.ui.auth.AuthViewModel
import com.example.projectthuctap.ui.home.HomeViewModel
import com.example.projectthuctap.ui.reminders.ReminderFragment

class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    private val homeViewModel: HomeViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProfileBinding {
        return FragmentProfileBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()
        setupClick()
        openReminder()
        homeViewModel.loadUser()
    }

    private fun setupClick() {
        binding.btnLogOut.setOnClickListener {
            authViewModel.logout()
        }
    }

    private fun openReminder() {
        binding.txtReminder.setOnClickListener {
            navigate(
                fragment = ReminderFragment(),
                containerId = R.id.fragment_container
            )
        }
    }

    private fun observeViewModel() {

        homeViewModel.userName.observe(viewLifecycleOwner) {
            binding.txtName.text = it
        }

        authViewModel.logoutSuccessLiveData.observe(viewLifecycleOwner) { success ->
            if (success == true) {
                showToast("Đã đăng xuất")
                goToLogin()
            }
        }
    }

    private fun goToLogin() {
        val intent = Intent(requireContext(), AuthActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }
}
