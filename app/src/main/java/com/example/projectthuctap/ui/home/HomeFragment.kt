package com.example.projectthuctap.ui.home

import android.app.DatePickerDialog
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.projectthuctap.R
import com.example.projectthuctap.ui.chatbot.ChatBotFragment
import com.example.projectthuctap.ui.transaction.AdjustTransactionFragment
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var viewModel: HomeViewModel

    private lateinit var layoutMonthFilter: LinearLayout
    private lateinit var txtSelectedMonth: TextView

    private lateinit var txtTotal: TextView
    private lateinit var txtIncome: TextView
    private lateinit var txtExpense: TextView

    private lateinit var txtName: TextView

    private lateinit var txtIncomeD: TextView
    private lateinit var txtExpenseD: TextView
    private lateinit var txtBalance: TextView
    private lateinit var barIncome: View
    private lateinit var barExpense: View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        bindView(view)
        observeViewModel()
        setupMonthFilter()

        val cal = Calendar.getInstance()

        viewModel.loadOverview()
        viewModel.loadUser()
        viewModel.loadDashboard(
            cal.get(Calendar.MONTH),
            cal.get(Calendar.YEAR)
        )

        txtTotal.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
                .replace(R.id.fragment_container, AdjustTransactionFragment())
                .addToBackStack(null)
                .commit()
        }
    }


    private fun bindView(view: View) {
        layoutMonthFilter = view.findViewById(R.id.layoutMonthFilter)
        txtSelectedMonth = view.findViewById(R.id.txtSelectedMonth)
        txtName = view.findViewById(R.id.txtName)

        txtTotal = view.findViewById(R.id.txtTotal)
        txtIncome = view.findViewById(R.id.txtIncome)
        txtExpense = view.findViewById(R.id.txtExpense)

        txtIncomeD = view.findViewById(R.id.txtIncomeD)
        txtExpenseD = view.findViewById(R.id.txtExpenseD)
        txtBalance = view.findViewById(R.id.txtBalance)

        barIncome = view.findViewById(R.id.barIncome)
        barExpense = view.findViewById(R.id.barExpense)
    }

    private fun observeViewModel() {

        viewModel.userName.observe(viewLifecycleOwner) {
            txtName.text = it
        }

        viewModel.totalBalance.observe(viewLifecycleOwner) {
            txtTotal.text = formatMoney(it)
        }

        viewModel.totalIncomeAll.observe(viewLifecycleOwner) {
            txtIncome.text = formatMoney(it)
        }

        viewModel.totalExpenseAll.observe(viewLifecycleOwner) {
            txtExpense.text = formatMoney(it)
        }

        viewModel.incomeMonth.observe(viewLifecycleOwner) {
            txtIncomeD.text = formatMoney(it)
            updateBars()
        }

        viewModel.expenseMonth.observe(viewLifecycleOwner) {
            txtExpenseD.text = formatMoney(it)
            updateBars()
        }

        viewModel.diffMonth.observe(viewLifecycleOwner) {
            txtBalance.text = formatMoney(it)
        }
    }

    private fun setupMonthFilter() {
        val cal = Calendar.getInstance()
        updateMonthText(cal.get(Calendar.MONTH), cal.get(Calendar.YEAR))

        layoutMonthFilter.setOnClickListener {
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
        txtSelectedMonth.text = "Tháng ${month + 1}/$year"
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

        barIncome.layoutParams = barIncome.layoutParams.apply {
            height = incomeHeight
        }

        barExpense.layoutParams = barExpense.layoutParams.apply {
            height = expenseHeight
        }

        barIncome.requestLayout()
        barExpense.requestLayout()
    }


    private fun dpToPx(dp: Int): Int =
        (dp * resources.displayMetrics.density).toInt()

    private fun formatMoney(value: Double): String =
        NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(value)
}

