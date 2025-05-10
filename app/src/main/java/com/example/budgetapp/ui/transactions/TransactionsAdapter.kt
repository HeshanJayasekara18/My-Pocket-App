package com.example.budgetapp.ui.transactions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetapp.R
import com.example.budgetapp.data.entity.Transaction
import com.example.budgetapp.data.entity.TransactionType
import com.example.budgetapp.databinding.ItemTransactionBinding
import java.text.SimpleDateFormat
import java.util.*

class TransactionsAdapter(
    private val onEditClick: (Transaction) -> Unit,
    private val onDeleteClick: (Transaction) -> Unit
) : ListAdapter<Transaction, TransactionsAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TransactionViewHolder(
        private val binding: ItemTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onEditClick(getItem(position))
                }
            }

            binding.btnDelete.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDeleteClick(getItem(position))
                }
            }
        }

        fun bind(transaction: Transaction) {
            binding.apply {
                // Set category icon based on transaction type
                val iconRes = if (transaction.type == TransactionType.INCOME) {
                    R.drawable.ic_income
                } else {
                    R.drawable.ic_expense
                }
                categoryIcon.setImageResource(iconRes)

                // Set background color based on transaction type
                val bgColor = if (transaction.type == TransactionType.INCOME) {
                    R.color.success_green_light
                } else {
                    R.color.error_red_light
                }
                categoryIcon.setBackgroundResource(bgColor)

                // Set icon color based on transaction type
                val iconColor = if (transaction.type == TransactionType.INCOME) {
                    R.color.success_green
                } else {
                    R.color.error_red
                }
                categoryIcon.setColorFilter(root.context.getColor(iconColor))

                // Set transaction details
                descriptionText.text = transaction.description
                dateText.text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    .format(Date(transaction.date))
                amountText.text = String.format("$%.2f", transaction.amount)
                amountText.setTextColor(root.context.getColor(iconColor))
            }
        }
    }

    private class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }
} 