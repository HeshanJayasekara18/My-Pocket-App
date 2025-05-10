package com.example.budgetapp.ui.transactions

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgetapp.MainActivity
import com.example.budgetapp.R
import com.example.budgetapp.data.AppDatabase
import com.example.budgetapp.data.entity.Transaction
import com.example.budgetapp.data.entity.TransactionType
import com.example.budgetapp.data.model.TransactionFilter
import com.example.budgetapp.data.repository.TransactionRepository
import com.example.budgetapp.databinding.FragmentTransactionsBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class TransactionsFragment : Fragment() {
    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: TransactionsViewModel
    private lateinit var transactionsAdapter: TransactionsAdapter
    private var selectedDate: Long = System.currentTimeMillis()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupRecyclerView()
        setupFilterChips()
        setupObservers()
        setupFab()
        loadTransactions()
    }

    private fun setupViewModel() {
        val database = AppDatabase.getDatabase(requireContext())
        val repository = TransactionRepository(database.transactionDao())
        val factory = TransactionsViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[TransactionsViewModel::class.java]
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
        binding.transactionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = transactionsAdapter
        }
    }

    private fun setupFilterChips() {
        binding.filterChipGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.allChip -> viewModel.setFilter(TransactionFilter.ALL)
                R.id.incomeChip -> viewModel.setFilter(TransactionFilter.INCOME)
                R.id.expenseChip -> viewModel.setFilter(TransactionFilter.EXPENSE)
            }
        }
    }

    private fun setupObservers() {
        viewModel.filteredTransactions.observe(viewLifecycleOwner) { transactions ->
            transactionsAdapter.submitList(transactions)
            updateEmptyState(transactions.isEmpty())
        }
    }

    private fun setupFab() {
        binding.addTransactionFab.setOnClickListener {
            showAddTransactionDialog()
        }
    }

    private fun loadTransactions() {
        val userId = (requireActivity() as MainActivity).getCurrentUserId()
        viewModel.loadTransactions(userId)
    }

    private fun showAddTransactionDialog() {
        val dialog = BottomSheetDialog(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.bottom_sheet_add_transaction, null)
        dialog.setContentView(dialogView)

        val etAmount = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etAmount)
        val etDescription = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etDescription)
        val etDate = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etDate)
        val chipGroup = dialogView.findViewById<com.google.android.material.chip.ChipGroup>(R.id.transactionTypeChipGroup)
        val spinnerCategory = dialogView.findViewById<android.widget.Spinner>(R.id.spinnerCategory)
        val btnAddTransaction = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnAddTransaction)

        // Set default date
        selectedDate = System.currentTimeMillis()
        etDate.setText(SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(selectedDate)))

        // Setup category spinner
        val categories = listOf("Food", "Transport", "Shopping", "Entertainment", "Bills", "Other")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter

        etDate.setOnClickListener {
            showDatePicker { date ->
                selectedDate = date
                etDate.setText(SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(date)))
            }
        }

        btnAddTransaction.setOnClickListener {
            try {
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

                val transaction = Transaction(
                    userId = (requireActivity() as MainActivity).getCurrentUserId(),
                    categoryId = (spinnerCategory.selectedItemPosition + 1).toLong(),
                    amount = amount,
                    description = description,
                    type = transactionType,
                    date = selectedDate
                )

                viewModel.addTransaction(transaction)
                dialog.dismiss()
                
                // Show success message
                Snackbar.make(binding.root, "Transaction added successfully", Snackbar.LENGTH_SHORT).show()
            } catch (e: Exception) {
                // Show error message
                Snackbar.make(binding.root, "Error adding transaction: ${e.message}", Snackbar.LENGTH_SHORT).show()
            }
        }

        dialog.show()
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

    private fun updateEmptyState(isEmpty: Boolean) {
        binding.emptyStateLayout.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.transactionsRecyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}