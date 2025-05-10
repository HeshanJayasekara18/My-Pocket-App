package com.example.budgetapp.ui.dashboard

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgetapp.MainActivity
import com.example.budgetapp.R
import com.example.budgetapp.data.entity.Transaction
import com.example.budgetapp.data.entity.TransactionType
import com.example.budgetapp.databinding.FragmentDashboardBinding
import com.example.budgetapp.ui.transactions.TransactionsAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DashboardViewModel
    private lateinit var transactionsAdapter: TransactionsAdapter
    private var selectedDate: Long = System.currentTimeMillis()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupViewModel()
        setupObservers()
        
        // Get current user data
        val mainActivity = activity as MainActivity
        val userName = mainActivity.getCurrentUserName()
        
        // Update welcome message
        binding.textWelcome.text = "Welcome, $userName!"
        
        // Load user's financial data
        viewModel.loadUserData(mainActivity.getCurrentUserId())
    }

    private fun setupRecyclerView() {
        transactionsAdapter = TransactionsAdapter(
            onEditClick = { transaction ->
                showAddEditTransactionDialog(transaction)
            },
            onDeleteClick = { transaction ->
                showDeleteConfirmationDialog(transaction)
            }
        )
        binding.recyclerTransactions.apply {
            adapter = transactionsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[DashboardViewModel::class.java]
    }

    private fun setupObservers() {
        viewModel.totalBalance.observe(viewLifecycleOwner) { balance ->
            binding.textTotalBalance.text = getString(R.string.currency_format, balance)
        }
        
        viewModel.totalIncome.observe(viewLifecycleOwner) { income ->
            binding.textIncome.text = getString(R.string.currency_format, income)
        }
        
        viewModel.totalExpenses.observe(viewLifecycleOwner) { expenses ->
            binding.textExpenses.text = getString(R.string.currency_format, expenses)
        }

        viewModel.recentTransactions.observe(viewLifecycleOwner) { transactions ->
            transactionsAdapter.submitList(transactions)
        }
    }

    private fun showAddEditTransactionDialog(transaction: Transaction) {
        val dialog = BottomSheetDialog(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.bottom_sheet_add_transaction, null)
        dialog.setContentView(dialogView)

        val etAmount = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etAmount)
        val etDescription = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etDescription)
        val etDate = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etDate)
        val chipGroup = dialogView.findViewById<com.google.android.material.chip.ChipGroup>(R.id.transactionTypeChipGroup)
        val btnAddTransaction = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnAddTransaction)

        // Pre-fill the fields
        etAmount.setText(transaction.amount.toString())
        etDescription.setText(transaction.description)
        selectedDate = transaction.date
        etDate.setText(SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(selectedDate)))
        
        // Set the transaction type chip
        chipGroup.check(
            when (transaction.type) {
                TransactionType.INCOME -> R.id.chipIncome
                TransactionType.EXPENSE -> R.id.chipExpense
            }
        )

        btnAddTransaction.text = "Update Transaction"

        etDate.setOnClickListener {
            showDatePicker { date ->
                selectedDate = date
                etDate.setText(SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(date)))
            }
        }

        btnAddTransaction.setOnClickListener {
            val amount = etAmount.text.toString().toDoubleOrNull()
            val description = etDescription.text.toString()
            val selectedChipId = chipGroup.checkedChipId

            if (amount == null || amount <= 0) {
                etAmount.error = "Please enter a valid amount"
                return@setOnClickListener
            }

            if (description.isBlank()) {
                etDescription.error = "Please enter a description"
                return@setOnClickListener
            }

            if (selectedChipId == View.NO_ID) {
                Snackbar.make(binding.root, "Please select a transaction type", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val transactionType = when (selectedChipId) {
                R.id.chipIncome -> TransactionType.INCOME
                R.id.chipExpense -> TransactionType.EXPENSE
                else -> TransactionType.EXPENSE
            }

            val updatedTransaction = transaction.copy(
                amount = amount,
                description = description,
                type = transactionType,
                date = selectedDate
            )

            viewModel.updateTransaction(updatedTransaction)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showDeleteConfirmationDialog(transaction: Transaction) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_transaction)
            .setMessage(R.string.delete_confirmation)
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.deleteTransaction(transaction)
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    private fun showDatePicker(onDateSelected: (Long) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                calendar.set(year, month, day)
                onDateSelected(calendar.timeInMillis)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 