package com.example.projectthuctap.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectthuctap.databinding.FragmentHistoryBinding
import com.example.projectthuctap.ui.adapter.HistoryAdapter
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.Calendar

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HistoryViewModel
    private lateinit var adapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[HistoryViewModel::class.java]

        setupRecyclerView()
        setupClick()
        observeData()

        viewModel.loadData()
    }

    private fun setupRecyclerView() {
        adapter = HistoryAdapter(requireContext())

        binding.rvHistoryT.layoutManager =
            LinearLayoutManager(requireContext())

        binding.rvHistoryT.adapter = adapter
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
            binding.txtIncomeHistory.text = "${it.toInt()}đ"
        }

        viewModel.expense.observe(viewLifecycleOwner) {
            binding.txtIncomExpense.text = "${it.toInt()}đ"
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
