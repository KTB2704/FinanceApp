package com.example.projectthuctap.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectthuctap.R
import com.example.projectthuctap.base.BaseFragment
import com.example.projectthuctap.data.model.Transaction
import com.example.projectthuctap.databinding.FragmentHistoryBinding
import com.example.projectthuctap.ui.adapter.HistoryAdapter
import com.example.projectthuctap.ui.transaction.EditTransactionFragment
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.Calendar

class HistoryFragment : BaseFragment<FragmentHistoryBinding>() {

    private val viewModel: HistoryViewModel by viewModels()
    private lateinit var adapter: HistoryAdapter

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHistoryBinding {
        return FragmentHistoryBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClick()
        observeData()

        viewModel.loadData()
    }

    private fun setupRecyclerView() {

        adapter = HistoryAdapter(

            onEditClick = { transaction: Transaction ->

                val bundle = Bundle().apply {
                    putSerializable("transaction", transaction)
                }

                navigate(
                    fragment = EditTransactionFragment().apply {
                        arguments = bundle
                    },
                    containerId = R.id.fragment_container
                )
            },

            onDeleteClick = { transaction: Transaction ->
                viewModel.deleteTransaction(transaction)
            }
        )

        binding.rvHistoryT.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HistoryFragment.adapter
        }
    }

    private fun setupClick() {
        binding.imgDate.setOnClickListener {
            showMonthPicker()
        }
    }

    private fun observeData() {

        viewModel.transactions.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.income.observe(viewLifecycleOwner) {
            binding.txtIncomeHistory.text = formatMoney(it)
        }

        viewModel.expense.observe(viewLifecycleOwner) {
            binding.txtIncomExpense.text = formatMoney(it)
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
