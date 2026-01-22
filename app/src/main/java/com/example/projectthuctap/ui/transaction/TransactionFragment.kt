package com.example.projectthuctap.ui.transaction

import Category
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
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

class TransactionFragment : Fragment(R.layout.fragment_transaction) {

    private lateinit var viewModel: TransactionViewModel

    private lateinit var rvCategory: RecyclerView
    private lateinit var imgSelectedIcon: ImageView
    private lateinit var tvSelectedName: TextView
    private lateinit var layoutSelectedCategory: View
    private lateinit var spinnerType: Spinner
    private lateinit var edtAmount: EditText
    private lateinit var edtDateTime: EditText
    private lateinit var etNote: EditText

    private lateinit var btnSave: Button

    private val calendar = Calendar.getInstance()
    private var isCategoryVisible = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[TransactionViewModel::class.java]

        bindView(view)
        setupRecyclerView()
        setupToggle()
        setupTypeSpinner()
        setupDateTimePicker()
        setDefaultDateTime()
        observeViewModel()

        viewModel.loadCategories()

        btnSave.setOnClickListener {
            viewModel.saveTransaction(
                edtAmount.text.toString(),
                etNote.text.toString(),
                calendar.timeInMillis
            )
        }
    }

    private fun bindView(view: View) {
        rvCategory = view.findViewById(R.id.rvCategory)
        imgSelectedIcon = view.findViewById(R.id.imgSelectedIcon)
        tvSelectedName = view.findViewById(R.id.tvSelectedName)
        layoutSelectedCategory = view.findViewById(R.id.layoutSelectedCategory)
        spinnerType = view.findViewById(R.id.spinnerItem)
        edtAmount = view.findViewById(R.id.edtAmount)
        edtDateTime = view.findViewById(R.id.edtDateTime)
        etNote = view.findViewById(R.id.etNote)
        btnSave = view.findViewById(R.id.btnSave)
    }

    private fun setupRecyclerView() {
        rvCategory.layoutManager = GridLayoutManager(requireContext(), 4)
    }

    private fun setupToggle() {
        layoutSelectedCategory.setOnClickListener {
            isCategoryVisible = !isCategoryVisible
            rvCategory.visibility = if (isCategoryVisible) View.VISIBLE else View.GONE
        }
    }

    private fun setupTypeSpinner() {
        spinnerType.onItemSelectedListener =
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
            rvCategory.adapter = CategoryAdapter(requireContext(), it) {
                viewModel.selectCategory(it)
            }
        }

        viewModel.selectedCategory.observe(viewLifecycleOwner) { category ->
            if (category == null) {
                resetSelectedCategory()
                return@observe
            }

            tvSelectedName.text = category.name
            val iconRes = resources.getIdentifier(
                category.icon,
                "drawable",
                requireContext().packageName
            )
            if (iconRes != 0) imgSelectedIcon.setImageResource(iconRes)

            rvCategory.visibility = View.GONE
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
            edtAmount.setTextColor(
                resources.getColor(android.R.color.holo_red_dark, null)
            )
            edtAmount.setHintTextColor(
                resources.getColor(android.R.color.holo_red_light, null)
            )
        } else {
            edtAmount.setTextColor(
                resources.getColor(android.R.color.holo_green_dark, null)
            )
            edtAmount.setHintTextColor(
                resources.getColor(android.R.color.holo_green_light, null)
            )
        }
    }

    private fun setupDateTimePicker() {
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
            SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(calendar.time)
        )
    }

    private fun resetSelectedCategory() {
        tvSelectedName.text = "Chọn hạng mục"
        imgSelectedIcon.setImageDrawable(null)
        rvCategory.visibility = View.VISIBLE
        isCategoryVisible = true
    }

    private fun clearForm() {
        edtAmount.setText("")
        etNote.setText("")
        resetSelectedCategory()
        calendar.timeInMillis = System.currentTimeMillis()
        setDefaultDateTime()
    }
}
