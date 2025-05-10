package com.example.budgetapp.ui.transactions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetapp.data.model.TransactionCategory
import com.example.budgetapp.databinding.ItemCategoryBinding

class CategoryAdapter(
    private val onCategorySelected: (TransactionCategory) -> Unit
) : ListAdapter<TransactionCategory, CategoryAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CategoryViewHolder(
        private val binding: ItemCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onCategorySelected(getItem(position))
                }
            }
        }

        fun bind(category: TransactionCategory) {
            binding.apply {
                ivCategoryIcon.setImageResource(category.iconResId)
                tvCategoryName.text = category.name
            }
        }
    }

    private class CategoryDiffCallback : DiffUtil.ItemCallback<TransactionCategory>() {
        override fun areItemsTheSame(oldItem: TransactionCategory, newItem: TransactionCategory): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TransactionCategory, newItem: TransactionCategory): Boolean {
            return oldItem == newItem
        }
    }
} 