package com.example.projectthuctap.ui.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.projectthuctap.R
import com.example.projectthuctap.base.BaseFragment
import com.example.projectthuctap.databinding.FragmentTransactionBinding
import com.example.projectthuctap.ui.adapter.CategoryAdapter
import com.example.projectthuctap.ui.chatbot.ChatBotFragment
import com.example.projectthuctap.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.*

class TransactionFragment :
    BaseFragment<FragmentTransactionBinding>() {

    private val viewModel: TransactionViewModel by viewModels()

    private val calendar = Calendar.getInstance()
    private var isCategoryVisible = true

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTransactionBinding {
        return FragmentTransactionBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            navigate(
                fragment = ChatBotFragment(),
                containerId = R.id.fragment_container
            )
        }
    }

    private fun setupRecyclerView() {
        binding.rvCategory.layoutManager =
            GridLayoutManager(requireContext(), 4)
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
                    val type =
                        if (position == 0) "expense" else "income"

                    updateAmountColor(type)
                    viewModel.setTransactionType(type)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    private fun observeViewModel() {

        viewModel.filteredCategories.observe(viewLifecycleOwner) { list ->
            binding.rvCategory.adapter =
                CategoryAdapter(requireContext(), list) { category ->
                    viewModel.selectCategory(category)
                }
        }

        viewModel.selectedCategory.observe(viewLifecycleOwner) { category ->

            if (category == null) {
                resetSelectedCategory()
                return@observe
            }

            binding.tvSelectedName.text = category.name

            val iconRes = requireContext().resources.getIdentifier(
                category.icon,
                "drawable",
                requireContext().packageName
            )

            if (iconRes != 0) {
                binding.imgSelectedIcon.setImageResource(iconRes)
            }

            binding.rvCategory.visibility = View.GONE
            isCategoryVisible = false
        }

        viewModel.message.observe(viewLifecycleOwner) { message ->
            showToast(message)
        }

        viewModel.success.observe(viewLifecycleOwner) { success ->
            if (success == true) {
                clearForm()
            }
        }
    }

    private fun updateAmountColor(type: String) {

        if (type == "expense") {
            binding.edtAmount.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    android.R.color.holo_red_dark
                )
            )
            binding.edtAmount.setHintTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    android.R.color.holo_red_light
                )
            )
        } else {
            binding.edtAmount.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    android.R.color.holo_green_dark
                )
            )
            binding.edtAmount.setHintTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    android.R.color.holo_green_light
                )
            )
        }
    }

    private fun setupDateTimePicker() {

        binding.edtDateTime.setOnClickListener {

            showDateTimePicker(calendar.timeInMillis) { time ->
                calendar.timeInMillis = time
                setDefaultDateTime()
            }
        }
    }

    private fun setDefaultDateTime() {
        val formatter = SimpleDateFormat(
            getString(R.string.date_time_format),
            Locale("vi", "VN")
        )

        binding.edtDateTime.setText(
            formatter.format(calendar.time)
        )
    }

    private fun resetSelectedCategory() {
        binding.tvSelectedName.text =
            getString(R.string.choose_category)

        binding.imgSelectedIcon.setImageDrawable(null)

        binding.rvCategory.visibility = View.VISIBLE
        isCategoryVisible = true
    }

    private fun clearForm() {
        binding.edtAmount.text?.clear()
        binding.etNote.text?.clear()

        resetSelectedCategory()

        calendar.timeInMillis = System.currentTimeMillis()
        setDefaultDateTime()
    }
}
