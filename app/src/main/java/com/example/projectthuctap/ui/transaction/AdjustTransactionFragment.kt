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
import com.example.projectthuctap.viewmodel.AdjustTransactionViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class AdjustTransactionFragment :
    Fragment(R.layout.fragment_adjust_transaction) {

    private lateinit var viewModel: AdjustTransactionViewModel

    private lateinit var rvCategory: RecyclerView
    private lateinit var spinnerType: Spinner
    private lateinit var edtDateTime: EditText
    private lateinit var etAmountReal: EditText
    private lateinit var txtTotalAmount: TextView
    private lateinit var txtColor: TextView
    private lateinit var txtAmount: TextView
    private lateinit var btnSave: Button
    private lateinit var btnBack: ImageView

    private lateinit var layoutSelectedCategory: LinearLayout
    private lateinit var imgSelectedIcon: ImageView
    private lateinit var tvSelectedName: TextView
    private lateinit var etNote: EditText

    private val calendar = Calendar.getInstance()
    private var adjustAmount = 0.0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel =
            ViewModelProvider(this)[AdjustTransactionViewModel::class.java]

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

        layoutSelectedCategory.setOnClickListener {
            rvCategory.visibility = View.VISIBLE
        }

        btnSave.setOnClickListener {

            if (adjustAmount == 0.0) return@setOnClickListener

            if (viewModel.selectedCategory.value == null) {
                Toast.makeText(requireContext(),
                    "Vui lòng chọn hạng mục",
                    Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.saveTransaction(
                adjustAmount,
                etNote.text.toString(),
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
        layoutSelectedCategory = view.findViewById(R.id.layoutSelectedCategory)
        imgSelectedIcon = view.findViewById(R.id.imgSelectedIcon)
        tvSelectedName = view.findViewById(R.id.tvSelectedName)
        etNote = view.findViewById(R.id.etNote)
    }

    private fun setupSpinner() {

        val types = listOf("Thu", "Chi")

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            types
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spinnerType.adapter = adapter

        spinnerType.onItemSelectedListener =
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

        viewModel.transactionType.observe(viewLifecycleOwner) { type ->
            val position = if (type == "income") 0 else 1
            if (spinnerType.selectedItemPosition != position) {
                spinnerType.setSelection(position)
            }
        }

        viewModel.adjustPreview.observe(viewLifecycleOwner) { pair ->

            val amount = pair.first
            val type = pair.second
            adjustAmount = amount

            if (amount == 0.0) {
                txtAmount.text = "0đ"
                txtColor.text = ""
                return@observe
            }

            txtAmount.text = formatMoney(amount)

            if (type == "income") {
                txtColor.text = "Đã thu"
                txtColor.setTextColor(
                    resources.getColor(android.R.color.holo_green_dark, null)
                )
                txtAmount.setTextColor(
                    resources.getColor(android.R.color.holo_green_dark, null)
                )
            } else {
                txtColor.text = "Đã chi"
                txtColor.setTextColor(
                    resources.getColor(android.R.color.holo_red_dark, null)
                )
                txtAmount.setTextColor(
                    resources.getColor(android.R.color.holo_red_dark, null)
                )
            }
        }

        viewModel.selectedCategory.observe(viewLifecycleOwner) { category ->

            if (category == null) {
                tvSelectedName.text = "Chọn hạng mục"
                imgSelectedIcon.setImageDrawable(null)
                rvCategory.visibility = View.VISIBLE
                return@observe
            }

            tvSelectedName.text = category.name

            val iconRes = resources.getIdentifier(
                category.icon,
                "drawable",
                requireContext().packageName
            )

            if (iconRes != 0) {
                imgSelectedIcon.setImageResource(iconRes)
            }

            rvCategory.visibility = View.GONE
        }

        viewModel.success.observe(viewLifecycleOwner) { success ->
            if (success == true) {

                Toast.makeText(
                    requireContext(),
                    "Đã lưu giao dịch",
                    Toast.LENGTH_SHORT
                ).show()

                etAmountReal.text?.clear()
                etNote.text?.clear()

                viewModel.resetState()

                parentFragmentManager.popBackStack()
            }
        }

        viewModel.message.observe(viewLifecycleOwner) { msg ->
            msg?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun formatMoney(amount: Double): String {
        return String.format("%,.0fđ", amount)
    }
}
