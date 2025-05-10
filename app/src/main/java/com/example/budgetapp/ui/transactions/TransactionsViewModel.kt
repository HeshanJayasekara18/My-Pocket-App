package com.example.budgetapp.ui.transactions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetapp.data.entity.Transaction
import com.example.budgetapp.data.entity.TransactionType
import com.example.budgetapp.data.model.TransactionFilter
import com.example.budgetapp.data.repository.TransactionRepository
import kotlinx.coroutines.launch

class TransactionsViewModel(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> = _transactions

    private val _filteredTransactions = MutableLiveData<List<Transaction>>()
    val filteredTransactions: LiveData<List<Transaction>> = _filteredTransactions

    private var currentFilter = TransactionFilter.ALL

    fun loadTransactions(userId: Long) {
        viewModelScope.launch {
            val transactions = transactionRepository.getTransactionsByUserId(userId)
            _transactions.value = transactions
            applyFilter(transactions)
        }
    }

    fun setFilter(filter: TransactionFilter) {
        currentFilter = filter
        _transactions.value?.let { applyFilter(it) }
    }

    private fun applyFilter(transactions: List<Transaction>) {
        _filteredTransactions.value = when (currentFilter) {
            TransactionFilter.ALL -> transactions
            TransactionFilter.INCOME -> transactions.filter { it.type == TransactionType.INCOME }
            TransactionFilter.EXPENSE -> transactions.filter { it.type == TransactionType.EXPENSE }
        }
    }

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.insertTransaction(transaction)
            loadTransactions(transaction.userId)
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.updateTransaction(transaction)
            loadTransactions(transaction.userId)
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.deleteTransaction(transaction)
            loadTransactions(transaction.userId)
        }
    }

    fun undoDeleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.insertTransaction(transaction)
            loadTransactions(transaction.userId)
        }
    }
} 