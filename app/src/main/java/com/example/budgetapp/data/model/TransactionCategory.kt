package com.example.budgetapp.data.model

import com.example.budgetapp.R
import com.example.budgetapp.data.entity.TransactionType

data class TransactionCategory(
    val id: Long,
    val name: String,
    val iconResId: Int,
    val type: TransactionType
)

object TransactionCategories {
    val incomeCategories = listOf(
        TransactionCategory(1L, "Salary", R.drawable.ic_salary, TransactionType.INCOME),
        TransactionCategory(2L, "Freelance", R.drawable.ic_freelance, TransactionType.INCOME),
        TransactionCategory(3L, "Investments", R.drawable.ic_investment, TransactionType.INCOME),
        TransactionCategory(4L, "Gifts", R.drawable.ic_gift, TransactionType.INCOME),
        TransactionCategory(5L, "Other Income", R.drawable.ic_other_income, TransactionType.INCOME)
    )

    val expenseCategories = listOf(
        TransactionCategory(6L, "Food", R.drawable.ic_food, TransactionType.EXPENSE),
        TransactionCategory(7L, "Transport", R.drawable.ic_transport, TransactionType.EXPENSE),
        TransactionCategory(8L, "Shopping", R.drawable.ic_shopping, TransactionType.EXPENSE),
        TransactionCategory(9L, "Entertainment", R.drawable.ic_entertainment, TransactionType.EXPENSE),
        TransactionCategory(10L, "Bills", R.drawable.ic_bills, TransactionType.EXPENSE),
        TransactionCategory(11L, "Health", R.drawable.ic_health, TransactionType.EXPENSE),
        TransactionCategory(12L, "Education", R.drawable.ic_education, TransactionType.EXPENSE),
        TransactionCategory(13L, "Other Expense", R.drawable.ic_other_expense, TransactionType.EXPENSE)
    )

    fun getCategoriesByType(type: TransactionType): List<TransactionCategory> {
        return when (type) {
            TransactionType.INCOME -> incomeCategories
            TransactionType.EXPENSE -> expenseCategories
        }
    }
} 