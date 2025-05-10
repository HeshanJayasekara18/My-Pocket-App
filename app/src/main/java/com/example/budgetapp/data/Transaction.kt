package com.example.budgetapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val amount: Double,
    val description: String,
    val category: String,
    val type: TransactionType,
    val date: Date
)

enum class TransactionType {
    INCOME,
    EXPENSE
} 