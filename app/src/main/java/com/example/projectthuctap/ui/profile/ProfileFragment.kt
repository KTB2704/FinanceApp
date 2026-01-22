package com.example.projectthuctap.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.projectthuctap.AuthActivity
import com.example.projectthuctap.MainActivity
import com.example.projectthuctap.R
import com.example.projectthuctap.ui.auth.AuthViewModel
import com.example.projectthuctap.ui.auth.login.LoginFragment
import com.example.projectthuctap.ui.home.HomeViewModel

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var viewModel: HomeViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var txtName: TextView
    private lateinit var btnLogOut: Button


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        bindView(view)
        observeViewModel()


        viewModel.loadUser()

        btnLogOut.setOnClickListener {
            authViewModel.logout()
        }

        authViewModel.logoutSuccessLiveData.observe(viewLifecycleOwner) {
            if (it) {
                Toast.makeText(requireContext(), "Đã đăng xuất", Toast.LENGTH_SHORT).show()
                goToLogin()
            }
        }
    }

    private fun observeViewModel() {

        viewModel.userName.observe(viewLifecycleOwner) {
            txtName.text = it
        }

    }

    private fun bindView(view: View) {
        txtName = view.findViewById(R.id.txtName)
        btnLogOut = view.findViewById(R.id.btnLogOut)
    }

    private fun goToLogin() {
        val intent = Intent(requireContext(), AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }



}

