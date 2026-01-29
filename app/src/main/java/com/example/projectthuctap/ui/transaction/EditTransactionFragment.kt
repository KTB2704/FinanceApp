package com.example.projectthuctap.ui.transaction

import Category
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
import com.example.projectthuctap.data.model.Transaction
import com.example.projectthuctap.databinding.FragmentEditTransactionBinding
import com.example.projectthuctap.ui.adapter.CategoryAdapter
import com.example.projectthuctap.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.*

class EditTransactionFragment :
    BaseFragment<FragmentEditTransactionBinding>() {

    private val viewModel: TransactionViewModel by viewModels()

    private val calendar = Calendar.getInstance()
    private var oldTransaction: Transaction? = null
    private var isFirstLoad = true
    private var isCategoryVisible = true

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentEditTransactionBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        oldTransaction =
            arguments?.getSerializable("transaction") as? Transaction

        setupRecyclerView()
        setupToggle()
        setupSpinner()
        setupDateTimePicker()
        observeViewModel()

        viewModel.loadCategories()

        binding.btnSave.setOnClickListener {
            oldTransaction?.let {
                viewModel.updateTransaction(
                    oldTransaction = it,
                    amount = binding.edtAmount.text.toString(),
                    note = binding.etNote.text.toString(),
                    time = calendar.timeInMillis
                )
            }
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


    private fun setupSpinner() {
        binding.spinnerItem.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val type =
                        if (position == 0) "expense"
                        else "income"

                    updateAmountColor(type)

                    // Nếu là lần load đầu tiên (edit) thì không reset category
                    viewModel.setTransactionType(type, isFirstLoad)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }


    private fun setupDateTimePicker() {
        binding.edtDateTime.setOnClickListener {
            showDateTimePicker(calendar.timeInMillis) { time ->
                calendar.timeInMillis = time
                updateDateTime()
            }
        }
    }


    private fun observeViewModel() {

        viewModel.filteredCategories.observe(viewLifecycleOwner) { list ->

            binding.rvCategory.adapter =
                CategoryAdapter(requireContext(), list) { category ->
                    viewModel.selectCategory(category)
                }

            if (isFirstLoad) {
                oldTransaction?.let {
                    loadOldData(it, list)
                }
                isFirstLoad = false
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

        viewModel.message.observe(viewLifecycleOwner) {
            showToast(it)
        }

        viewModel.success.observe(viewLifecycleOwner) {
            if (it == true) popBack()
        }
    }

    // ---------------- Load Old Data ----------------

    private fun loadOldData(
        transaction: Transaction,
        categoryList: List<Category>
    ) {

        binding.edtAmount.setText(transaction.amount.toLong().toString())
        binding.etNote.setText(transaction.note)

        calendar.timeInMillis = transaction.timestamp
        updateDateTime()

        val position =
            if (transaction.type == "expense") 0 else 1
        binding.spinnerItem.setSelection(position)

        updateAmountColor(transaction.type)

        val matchedCategory = categoryList.find {
            it.id == transaction.categoryId
        }

        matchedCategory?.let {
            viewModel.selectCategory(it)
        }
    }


    private fun updateDateTime() {
        val formatter = SimpleDateFormat(
            getString(R.string.date_time_format),
            Locale("vi", "VN")
        )

        binding.edtDateTime.setText(
            formatter.format(calendar.time)
        )
    }

    private fun updateAmountColor(type: String) {

        val textColor =
            if (type == "expense")
                android.R.color.holo_red_dark
            else
                android.R.color.holo_green_dark

        val hintColor =
            if (type == "expense")
                android.R.color.holo_red_light
            else
                android.R.color.holo_green_light

        binding.edtAmount.setTextColor(
            ContextCompat.getColor(requireContext(), textColor)
        )

        binding.edtAmount.setHintTextColor(
            ContextCompat.getColor(requireContext(), hintColor)
        )
    }

    private fun resetSelectedCategory() {
        binding.tvSelectedName.text =
            getString(R.string.choose_category)

        binding.imgSelectedIcon.setImageDrawable(null)

        binding.rvCategory.visibility = View.VISIBLE
        isCategoryVisible = true
    }
}
