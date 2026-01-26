package com.example.projectthuctap.ui.transaction

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.projectthuctap.R
import com.example.projectthuctap.base.BaseFragment
import com.example.projectthuctap.databinding.FragmentAdjustTransactionBinding
import com.example.projectthuctap.ui.adapter.CategoryAdapter
import com.example.projectthuctap.viewmodel.AdjustTransactionViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AdjustTransactionFragment :
    BaseFragment<FragmentAdjustTransactionBinding>() {

    private lateinit var viewModel: AdjustTransactionViewModel

    private val calendar = Calendar.getInstance()
    private var adjustAmount = 0.0

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAdjustTransactionBinding {
        return FragmentAdjustTransactionBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel =
            ViewModelProvider(this)[AdjustTransactionViewModel::class.java]

        setupSpinner()
        setupRecyclerView()
        setupDateTimePicker()
        setupTextWatcher()
        observeViewModel()

        viewModel.loadCategories()
        viewModel.loadCurrentBalance()

        binding.btnBack.setOnClickListener {
            popBack()
        }

        binding.layoutSelectedCategory.setOnClickListener {
            binding.rvCategory.visibility = View.VISIBLE
        }

        binding.btnSave.setOnClickListener {

            if (adjustAmount == 0.0) return@setOnClickListener

            if (viewModel.selectedCategory.value == null) {
                showToast(getString(R.string.please_choose_category))
                return@setOnClickListener
            }

            viewModel.saveTransaction(
                adjustAmount,
                binding.etNote.text.toString(),
                calendar.timeInMillis
            )
        }
    }

    private fun setupSpinner() {

        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.transaction_types,
            android.R.layout.simple_spinner_item
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        binding.spinnerItem.adapter = adapter

        binding.spinnerItem.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val type =
                        if (position == 0) "income" else "expense"

                    viewModel.setTransactionType(type)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    private fun setupRecyclerView() {
        binding.rvCategory.layoutManager =
            GridLayoutManager(requireContext(), 4)
    }

    private fun setupDateTimePicker() {

        setDefaultDateTime()

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
            SimpleDateFormat(
                getString(R.string.date_time_format),
                Locale("vi", "VN")
            ).format(calendar.time)
        )
    }

    private fun setupTextWatcher() {

        binding.etAmountReal.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) {}

            override fun onTextChanged(
                s: CharSequence?, start: Int, before: Int, count: Int
            ) {}

            override fun afterTextChanged(s: Editable?) {
                viewModel.calculateAdjust(s.toString())
            }
        })
    }

    private fun observeViewModel() {

        viewModel.totalBalance.observe(viewLifecycleOwner) {
            binding.txtTotalAmount.text = formatMoney(it)
        }

        viewModel.filteredCategories.observe(viewLifecycleOwner) {
            binding.rvCategory.adapter =
                CategoryAdapter(requireContext(), it) { category ->
                    viewModel.selectCategory(category)
                }
        }

        viewModel.transactionType.observe(viewLifecycleOwner) { type ->
            val position = if (type == "income") 0 else 1
            if (binding.spinnerItem.selectedItemPosition != position) {
                binding.spinnerItem.setSelection(position)
            }
        }

        viewModel.adjustPreview.observe(viewLifecycleOwner) { pair ->

            val amount = pair.first
            val type = pair.second
            adjustAmount = amount

            if (amount == 0.0) {
                binding.txtAmount.text =
                    getString(R.string.money_zero)
                binding.txtColor.text = ""
                return@observe
            }

            binding.txtAmount.text = formatMoney(amount)

            if (type == "income") {

                binding.txtColor.text =
                    getString(R.string.already_income)

                binding.txtColor.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        android.R.color.holo_green_dark
                    )
                )

                binding.txtAmount.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        android.R.color.holo_green_dark
                    )
                )

            } else {

                binding.txtColor.text =
                    getString(R.string.already_expense)

                binding.txtColor.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        android.R.color.holo_red_dark
                    )
                )

                binding.txtAmount.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        android.R.color.holo_red_dark
                    )
                )
            }
        }

        viewModel.selectedCategory.observe(viewLifecycleOwner) { category ->

            if (category == null) {

                binding.tvSelectedName.text =
                    getString(R.string.choose_category)

                binding.imgSelectedIcon.setImageDrawable(null)
                binding.rvCategory.visibility = View.VISIBLE
                return@observe
            }

            binding.tvSelectedName.text = category.name

            val iconRes = resources.getIdentifier(
                category.icon,
                "drawable",
                requireContext().packageName
            )

            if (iconRes != 0) {
                binding.imgSelectedIcon.setImageResource(iconRes)
            }

            binding.rvCategory.visibility = View.GONE
        }

        viewModel.success.observe(viewLifecycleOwner) { success ->
            if (success == true) {

                showToast(getString(R.string.transaction_saved))

                binding.etAmountReal.text?.clear()
                binding.etNote.text?.clear()

                viewModel.resetState()

                popBack()
            }
        }

        viewModel.message.observe(viewLifecycleOwner) { msg ->
            msg?.let {
                showToast(it)
            }
        }
    }
}
