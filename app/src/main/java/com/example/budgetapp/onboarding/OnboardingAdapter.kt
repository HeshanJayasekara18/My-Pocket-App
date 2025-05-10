package com.example.budgetapp.onboarding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.budgetapp.R
import com.example.budgetapp.databinding.ItemOnboardingBinding

class OnboardingAdapter : RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    private val onboardingItems = listOf(
        OnboardingItem(
            R.drawable.onboarding_1,
            "Welcome to Budget App",
            "Track your expenses and manage your budget effectively"
        ),
        OnboardingItem(
            R.drawable.onboarding_2,
            "Set Your Budget",
            "Create and manage your monthly budgets with ease"
        ),
        OnboardingItem(
            R.drawable.onboarding_3,
            "Track Expenses",
            "Keep track of your daily expenses and income"
        )
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val binding = ItemOnboardingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OnboardingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        holder.bind(onboardingItems[position])
    }

    override fun getItemCount(): Int = onboardingItems.size

    class OnboardingViewHolder(
        private val binding: ItemOnboardingBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: OnboardingItem) {
            Glide.with(binding.imageView.context)
                .load(item.imageResId)
                .centerInside()
                .into(binding.imageView)
            binding.titleTextView.text = item.title
            binding.descriptionTextView.text = item.description
        }
    }
}

data class OnboardingItem(
    val imageResId: Int,
    val title: String,
    val description: String
) 