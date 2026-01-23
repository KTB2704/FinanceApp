package com.example.projectthuctap.ui.transaction

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectthuctap.R
import com.example.projectthuctap.ui.adapter.CategoryAdapter
import com.example.projectthuctap.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.*

class AdjustTransactionFragment :
    Fragment(R.layout.fragment_adjust_transaction) {

    private lateinit var viewModel: TransactionViewModel

    private lateinit var rvCategory: RecyclerView
    private lateinit var spinnerType: Spinner
    private lateinit var edtDateTime: EditText
    private lateinit var etAmountReal: EditText
    private lateinit var txtTotalAmount: TextView
    private lateinit var txtColor: TextView
    private lateinit var txtAmount: TextView
    private lateinit var btnSave: Button
    private lateinit var btnBack: ImageView

    private val calendar = Calendar.getInstance()
    private var adjustAmount = 0.0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel =
            ViewModelProvider(requireActivity())[TransactionViewModel::class.java]

        bindView(view)
        setupSpinner()
        setupRecyclerView()
        setupDateTimePicker()
        setupTextWatcher()
        observeViewModel()

        viewModel.loadCategories()
        viewModel.loadCurrentBalance()

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnSave.setOnClickListener {

            if (adjustAmount == 0.0) return@setOnClickListener

            viewModel.saveTransaction(
                adjustAmount.toString(),
                "Điều chỉnh số dư",
                calendar.timeInMillis
            )
        }
    }

    private fun bindView(view: View) {
        rvCategory = view.findViewById(R.id.rvCategory)
        spinnerType = view.findViewById(R.id.spinnerItem)
        edtDateTime = view.findViewById(R.id.edtDateTime)
        etAmountReal = view.findViewById(R.id.etAmountReal)
        txtTotalAmount = view.findViewById(R.id.txtTotalAmount)
        txtColor = view.findViewById(R.id.txtColor)
        txtAmount = view.findViewById(R.id.txtAmount)
        btnSave = view.findViewById(R.id.btnSave)
        btnBack = view.findViewById(R.id.btnBack)
    }


    private fun setupSpinner() {

        val types = listOf("Thu", "Chi")

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            types
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType.adapter = adapter

        spinnerType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    val type = if (position == 0) "income" else "expense"

                    if (viewModel.transactionType.value != type) {
                        viewModel.setTransactionType(type)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    // ================= RECYCLER =================

    private fun setupRecyclerView() {
        rvCategory.layoutManager =
            GridLayoutManager(requireContext(), 4)
    }


    private fun setupDateTimePicker() {

        setDefaultDateTime()

        edtDateTime.setOnClickListener {

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
        edtDateTime.setText(
            SimpleDateFormat(
                "dd/MM/yyyy HH:mm",
                Locale.getDefault()
            ).format(calendar.time)
        )
    }


    private fun setupTextWatcher() {

        etAmountReal.addTextChangedListener(object : TextWatcher {

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
            txtTotalAmount.text = formatMoney(it)
        }

        viewModel.filteredCategories.observe(viewLifecycleOwner) {
            rvCategory.adapter =
                CategoryAdapter(requireContext(), it) { category ->
                    viewModel.selectCategory(category)
                }
        }

        // ⭐ Spinner sync theo transactionType
        viewModel.transactionType.observe(viewLifecycleOwner) { type ->

            val position = if (type == "income") 0 else 1

            if (spinnerType.selectedItemPosition != position) {
                spinnerType.setSelection(position)
            }
        }

        // ⭐ Preview đổi màu + set type
        viewModel.adjustPreview.observe(viewLifecycleOwner) {

            val amount = it.first
            val type = it.second

            adjustAmount = amount
            txtAmount.text = formatMoney(amount)

            when (type) {

                "income" -> {
                    txtColor.text = "Đã thu"
                    txtColor.setTextColor(
                        resources.getColor(android.R.color.holo_green_dark, null)
                    )
                    txtAmount.setTextColor(
                        resources.getColor(android.R.color.holo_green_dark, null)
                    )

                    viewModel.setTransactionType("income")
                }

                "expense" -> {
                    txtColor.text = "Đã chi"
                    txtColor.setTextColor(
                        resources.getColor(android.R.color.holo_red_dark, null)
                    )
                    txtAmount.setTextColor(
                        resources.getColor(android.R.color.holo_red_dark, null)
                    )

                    viewModel.setTransactionType("expense")
                }

                else -> {
                    txtColor.text = ""
                    txtAmount.text = "0đ"
                }
            }
        }

        viewModel.success.observe(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }
    }

    private fun formatMoney(amount: Double): String {
        return String.format("%,.0fđ", amount)
    }
}
