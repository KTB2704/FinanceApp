package ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.projectthuctap.MainActivity
import com.example.projectthuctap.R
import viewmodel.AuthViewModel

class LoginFragment : Fragment() {

    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_login, container, false)

        val btnRe = view.findViewById<Button>(R.id.btnRe)
        val etEmail = view.findViewById<EditText>(R.id.etEmail)
        val etPassword = view.findViewById<EditText>(R.id.etPassword)
        val btnLogin = view.findViewById<Button>(R.id.btnLog)

        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        btnRe.setOnClickListener {
            requireActivity()
                .supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
                .replace(R.id.auth_container, RegisterFragment())
                .addToBackStack(null)
                .commit()
        }

        btnLogin.setOnClickListener {
            viewModel.login(
                etEmail.text.toString().trim(),
                etPassword.text.toString().trim()
            )
        }

        viewModel.userLiveData.observe(viewLifecycleOwner) {
            val intent = Intent(requireActivity(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        viewModel.errorLiveData.observe(viewLifecycleOwner) { msg ->
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }

        return view
    }
}
