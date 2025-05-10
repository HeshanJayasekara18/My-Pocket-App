package com.example.budgetapp.ui.budget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgetapp.R
import com.example.budgetapp.databinding.FragmentBudgetBinding
import com.example.budgetapp.data.Budget

class BudgetFragment : Fragment() {
    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!
    private lateinit var budgetAdapter: BudgetAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupClickListeners()
        loadBudgets()
    }

    private fun setupRecyclerView() {
        budgetAdapter = BudgetAdapter()
        binding.budgetRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = budgetAdapter
        }
    }

    private fun setupClickListeners() {
        binding.addBudgetFab.setOnClickListener {
            // TODO: Show add budget dialog
        }
    }

    private fun loadBudgets() {
        // TODO: Load budgets from database
        val budgets = listOf(
            Budget(category = "Food", amount = 500.0, spent = 200.0),
            Budget(category = "Transport", amount = 300.0, spent = 150.0),
            Budget(category = "Shopping", amount = 400.0, spent = 250.0)
        )

        if (budgets.isEmpty()) {
            binding.emptyStateLayout.visibility = View.VISIBLE
            binding.budgetRecyclerView.visibility = View.GONE
        } else {
            binding.emptyStateLayout.visibility = View.GONE
            binding.budgetRecyclerView.visibility = View.VISIBLE
            budgetAdapter.submitList(budgets)
        }

        val totalBudget = budgets.sumOf { it.amount }
        binding.totalBudgetText.text = getString(R.string.total_budget_format, totalBudget)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 