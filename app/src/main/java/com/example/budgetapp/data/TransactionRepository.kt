package com.example.budgetapp.data

import com.example.budgetapp.data.dao.TransactionDao
import com.example.budgetapp.data.entity.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TransactionRepository(private val transactionDao: TransactionDao) {
    
    suspend fun getTransactionsByUserId(userId: Long): List<Transaction> = withContext(Dispatchers.IO) {
        transactionDao.getTransactionsByUserId(userId)
    }

    suspend fun insertTransaction(transaction: Transaction) = withContext(Dispatchers.IO) {
        transactionDao.insert(transaction)
    }

    suspend fun updateTransaction(transaction: Transaction) = withContext(Dispatchers.IO) {
        transactionDao.update(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction) = withContext(Dispatchers.IO) {
        transactionDao.delete(transaction)
    }
} 