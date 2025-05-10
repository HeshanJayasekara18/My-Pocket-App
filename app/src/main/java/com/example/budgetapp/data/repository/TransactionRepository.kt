package com.example.budgetapp.data.repository

import com.example.budgetapp.data.dao.TransactionDao
import com.example.budgetapp.data.entity.Transaction

class TransactionRepository(private val transactionDao: TransactionDao) {
    
    suspend fun getTransactionsByUserId(userId: Long): List<Transaction> {
        return transactionDao.getTransactionsByUserId(userId)
    }

    suspend fun insertTransaction(transaction: Transaction) {
        transactionDao.insert(transaction)
    }

    suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.update(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.delete(transaction)
    }
} 