package com.example.budgetapp.ui.budget

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetapp.R
import com.example.budgetapp.data.Budget
import com.example.budgetapp.databinding.ItemBudgetBinding

class BudgetAdapter : ListAdapter<Budget, BudgetAdapter.BudgetViewHolder>(BudgetDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        val binding = ItemBudgetBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BudgetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class BudgetViewHolder(
        private val binding: ItemBudgetBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(budget: Budget) {
            binding.apply {
                categoryText.text = budget.category
                amountText.text = root.context.getString(R.string.currency_format, budget.amount)
                spentText.text = root.context.getString(R.string.currency_format, budget.spent)
                remainingText.text = root.context.getString(R.string.currency_format, budget.remaining)
                
                // Calculate progress
                val progress = if (budget.amount > 0) {
                    (budget.spent / budget.amount * 100).toInt()
                } else 0
                progressBar.progress = progress
            }
        }
    }

    private class BudgetDiffCallback : DiffUtil.ItemCallback<Budget>() {
        override fun areItemsTheSame(oldItem: Budget, newItem: Budget): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Budget, newItem: Budget): Boolean {
            return oldItem == newItem
        }
    }
} 