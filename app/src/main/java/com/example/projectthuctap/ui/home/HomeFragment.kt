package com.example.projectthuctap.ui.home

import android.app.DatePickerDialog
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.projectthuctap.R
import com.example.projectthuctap.base.BaseFragment
import com.example.projectthuctap.databinding.FragmentHomeBinding
import com.example.projectthuctap.ui.transaction.AdjustTransactionFragment
import java.util.Calendar

class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private val viewModel: HomeViewModel by viewModels()

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()
        setupMonthFilter()
        setupClick()

        val cal = Calendar.getInstance()

        viewModel.loadOverview()
        viewModel.loadUser()
        viewModel.loadDashboard(
            cal.get(Calendar.MONTH),
            cal.get(Calendar.YEAR)
        )
    }

    private fun setupClick() {
        binding.txtTotal.setOnClickListener {
            navigate(
                AdjustTransactionFragment(),
                R.id.fragment_container
            )
        }
    }

    private fun observeViewModel() {

        viewModel.userName.observe(viewLifecycleOwner) {
            binding.txtName.text = it
        }

        viewModel.totalBalance.observe(viewLifecycleOwner) {
            binding.txtTotal.text = formatMoney(it)
        }

        viewModel.totalIncomeAll.observe(viewLifecycleOwner) {
            binding.txtIncome.text = formatMoney(it)
        }

        viewModel.totalExpenseAll.observe(viewLifecycleOwner) {
            binding.txtExpense.text = formatMoney(it)
        }

        viewModel.incomeMonth.observe(viewLifecycleOwner) {
            binding.txtIncomeD.text = formatMoney(it)
            updateBars()
        }

        viewModel.expenseMonth.observe(viewLifecycleOwner) {
            binding.txtExpenseD.text = formatMoney(it)
            updateBars()
        }

        viewModel.diffMonth.observe(viewLifecycleOwner) {
            binding.txtBalance.text = formatMoney(it)
        }
    }

    private fun setupMonthFilter() {
        val cal = Calendar.getInstance()
        updateMonthText(cal.get(Calendar.MONTH), cal.get(Calendar.YEAR))

        binding.layoutMonthFilter.setOnClickListener {
            showMonthPicker()
        }
    }

    private fun showMonthPicker() {
        val cal = Calendar.getInstance()

        val dialog = DatePickerDialog(
            requireContext(),
            { _, year, month, _ ->
                updateMonthText(month, year)
                viewModel.loadDashboard(month, year)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )

        dialog.datePicker.findViewById<View>(
            Resources.getSystem().getIdentifier("day", "id", "android")
        )?.visibility = View.GONE

        dialog.show()
    }

    private fun updateMonthText(month: Int, year: Int) {
        binding.txtSelectedMonth.text =
            getString(R.string.month_year_format, month + 1, year)
    }

    private fun updateBars() {
        val income = viewModel.incomeMonth.value ?: 0.0
        val expense = viewModel.expenseMonth.value ?: 0.0

        val maxValue = maxOf(income, expense)
        val maxHeight = dpToPx(120)

        val incomeHeight =
            if (maxValue == 0.0) dpToPx(4)
            else ((income / maxValue) * maxHeight).toInt()

        val expenseHeight =
            if (maxValue == 0.0) dpToPx(4)
            else ((expense / maxValue) * maxHeight).toInt()

        binding.barIncome.layoutParams =
            binding.barIncome.layoutParams.apply {
                height = incomeHeight
            }

        binding.barExpense.layoutParams =
            binding.barExpense.layoutParams.apply {
                height = expenseHeight
            }

        binding.barIncome.requestLayout()
        binding.barExpense.requestLayout()
    }
}