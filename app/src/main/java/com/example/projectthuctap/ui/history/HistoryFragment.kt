package com.example.projectthuctap.ui.history

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectthuctap.R
import com.example.projectthuctap.ui.adapter.HistoryAdapter

class HistoryFragment : Fragment(R.layout.fragment_history) {

    private lateinit var viewModel: HistoryViewModel
    private lateinit var adapter: HistoryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[HistoryViewModel::class.java]

        val rv = view.findViewById<RecyclerView>(R.id.rvHistoryT)
        adapter = HistoryAdapter(requireContext())
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

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
}
