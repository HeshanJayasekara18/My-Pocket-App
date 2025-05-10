package com.example.budgetapp.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.budgetapp.data.AppDatabase
import com.example.budgetapp.data.TransactionRepository
import com.example.budgetapp.data.entity.Transaction
import com.example.budgetapp.data.entity.TransactionType
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val _totalBalance = MutableLiveData<Double>()
    val totalBalance: LiveData<Double> = _totalBalance

    private val _totalIncome = MutableLiveData<Double>()
    val totalIncome: LiveData<Double> = _totalIncome

    private val _totalExpenses = MutableLiveData<Double>()
    val totalExpenses: LiveData<Double> = _totalExpenses

    private val _recentTransactions = MutableLiveData<List<Transaction>>()
    val recentTransactions: LiveData<List<Transaction>> = _recentTransactions

    private val transactionRepository: TransactionRepository

    init {
        val database = AppDatabase.getDatabase(application)
        transactionRepository = TransactionRepository(database.transactionDao())
    }

    fun loadUserData(userId: Long) {
        viewModelScope.launch {
            try {
                // Load user's financial data
                val transactions = transactionRepository.getTransactionsByUserId(userId)
                
                // Calculate totals
                var income = 0.0
                var expenses = 0.0
                
                transactions.forEach { transaction ->
                    when (transaction.type) {
                        TransactionType.INCOME -> income += transaction.amount
                        TransactionType.EXPENSE -> expenses += transaction.amount
                    }
                }
                
                // Update LiveData
                _totalIncome.value = income
                _totalExpenses.value = expenses
                _totalBalance.value = income - expenses

                // Update recent transactions (last 5 transactions)
                _recentTransactions.value = transactions.take(5)
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.updateTransaction(transaction)
            loadUserData(transaction.userId)
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.deleteTransaction(transaction)
            loadUserData(transaction.userId)
        }
    }
} 