package com.example.projectthuctap.ui.history

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectthuctap.R
import com.example.projectthuctap.ui.adapter.HistoryAdapter
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.Calendar

class HistoryFragment : Fragment(R.layout.fragment_history) {

    private lateinit var viewModel: HistoryViewModel
    private lateinit var adapter: HistoryAdapter

    private lateinit var imgDate: ImageView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[HistoryViewModel::class.java]

        val rv = view.findViewById<RecyclerView>(R.id.rvHistoryT)
        adapter = HistoryAdapter(requireContext())
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        imgDate = view.findViewById(R.id.imgDate)

        imgDate.setOnClickListener {
             showMonthPicker()
        }


        observeData()

        viewModel.loadData()

    }

    private fun observeData() {
        viewModel.transactions.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.income.observe(viewLifecycleOwner) {
            view?.findViewById<TextView>(R.id.txtIncomeHistory)
                ?.text = "${it.toInt()}đ"
        }

        viewModel.expense.observe(viewLifecycleOwner) {
            view?.findViewById<TextView>(R.id.txtIncomExpense)
                ?.text = "${it.toInt()}đ"
        }
    }

    private fun showMonthPicker() {
        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Chọn tháng")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        picker.show(parentFragmentManager, "MONTH_PICKER")

        picker.addOnPositiveButtonClickListener { timeInMillis ->
            val cal = Calendar.getInstance()
            cal.timeInMillis = timeInMillis

            val month = cal.get(Calendar.MONTH)
            val year = cal.get(Calendar.YEAR)

            viewModel.loadByMonth(month, year)
        }
    }

}


