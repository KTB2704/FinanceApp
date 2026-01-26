package com.example.projectthuctap.ui.transaction

import Category
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.projectthuctap.R
import com.example.projectthuctap.databinding.FragmentTransactionBinding
import com.example.projectthuctap.ui.adapter.CategoryAdapter
import com.example.projectthuctap.ui.chatbot.ChatBotFragment
import com.example.projectthuctap.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.*

class TransactionFragment : Fragment() {

    private var _binding: FragmentTransactionBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: TransactionViewModel

    private val calendar = Calendar.getInstance()
    private var isCategoryVisible = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[TransactionViewModel::class.java]

        setupRecyclerView()
        setupToggle()
        setupTypeSpinner()
        setupDateTimePicker()
        setDefaultDateTime()
        observeViewModel()

        viewModel.loadCategories()

        binding.btnSave.setOnClickListener {
            viewModel.saveTransaction(
                binding.edtAmount.text.toString(),
                binding.etNote.text.toString(),
                calendar.timeInMillis
            )
        }

        binding.btnChatBot.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
                .replace(R.id.fragment_container, ChatBotFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setupRecyclerView() {
        binding.rvCategory.layoutManager = GridLayoutManager(requireContext(), 4)
    }

    private fun setupToggle() {
        binding.layoutSelectedCategory.setOnClickListener {
            isCategoryVisible = !isCategoryVisible
            binding.rvCategory.visibility =
                if (isCategoryVisible) View.VISIBLE else View.GONE
        }
    }

    private fun setupTypeSpinner() {
        binding.spinnerItem.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val type = if (position == 0) "expense" else "income"
                    updateAmountColor(type)
                    viewModel.setTransactionType(type)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    private fun observeViewModel() {

        viewModel.filteredCategories.observe(viewLifecycleOwner) {
            binding.rvCategory.adapter =
                CategoryAdapter(requireContext(), it) {
                    viewModel.selectCategory(it)
                }
        }

        viewModel.selectedCategory.observe(viewLifecycleOwner) { category ->

            if (category == null) {
                resetSelectedCategory()
                return@observe
            }

            binding.tvSelectedName.text = category.name

            val iconRes = resources.getIdentifier(
                category.icon,
                "drawable",
                requireContext().packageName
            )

            if (iconRes != 0)
                binding.imgSelectedIcon.setImageResource(iconRes)

            binding.rvCategory.visibility = View.GONE
            isCategoryVisible = false
        }

        viewModel.message.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }

        viewModel.success.observe(viewLifecycleOwner) {
            clearForm()
        }
    }

    private fun updateAmountColor(type: String) {

        if (type == "expense") {
            binding.edtAmount.setTextColor(
                resources.getColor(android.R.color.holo_red_dark, null)
            )
            binding.edtAmount.setHintTextColor(
                resources.getColor(android.R.color.holo_red_light, null)
            )
        } else {
            binding.edtAmount.setTextColor(
                resources.getColor(android.R.color.holo_green_dark, null)
            )
            binding.edtAmount.setHintTextColor(
                resources.getColor(android.R.color.holo_green_light, null)
            )
        }
    }

    private fun setupDateTimePicker() {

        binding.edtDateTime.setOnClickListener {

            DatePickerDialog(
                requireContext(),
                { _, y, m, d ->
                    calendar.set(y, m, d)

                    TimePickerDialog(
                        requireContext(),
                        { _, h, min ->
                            calendar.set(Calendar.HOUR_OF_DAY, h)
                            calendar.set(Calendar.MINUTE, min)
                            setDefaultDateTime()
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                    ).show()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setDefaultDateTime() {
        binding.edtDateTime.setText(
            SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(calendar.time)
        )
    }

    private fun resetSelectedCategory() {
        binding.tvSelectedName.text = "Chọn hạng mục"
        binding.imgSelectedIcon.setImageDrawable(null)
        binding.rvCategory.visibility = View.VISIBLE
        isCategoryVisible = true
    }

    private fun clearForm() {
        binding.edtAmount.setText("")
        binding.etNote.setText("")
        resetSelectedCategory()
        calendar.timeInMillis = System.currentTimeMillis()
        setDefaultDateTime()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
