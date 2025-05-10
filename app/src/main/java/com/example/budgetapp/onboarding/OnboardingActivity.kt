package com.example.budgetapp.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.budgetapp.R
import com.example.budgetapp.auth.LoginActivity
import com.example.budgetapp.databinding.ActivityOnboardingBinding
import com.google.android.material.tabs.TabLayoutMediator
import android.view.View

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var onboardingAdapter: OnboardingAdapter
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
        setupButtons()
    }

    private fun setupViewPager() {
        onboardingAdapter = OnboardingAdapter()
        viewPager = binding.viewPager
        viewPager.adapter = onboardingAdapter

        // Add a creative page transformer for animation
        viewPager.setPageTransformer { page, position ->
            page.alpha = 0.25f + (1 - Math.abs(position))
            page.translationX = -50 * position
            page.scaleY = 0.85f + (1 - Math.abs(position)) * 0.15f
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ -> }.attach()

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val isLast = position == onboardingAdapter.itemCount - 1
                binding.buttonNext.text = if (isLast) {
                    getString(R.string.get_started)
                } else {
                    getString(R.string.next)
                }
                binding.buttonSkip.visibility = if (isLast) View.INVISIBLE else View.VISIBLE
            }
        })
    }

    private fun setupButtons() {
        binding.buttonNext.setOnClickListener {
            if (viewPager.currentItem + 1 < onboardingAdapter.itemCount) {
                viewPager.currentItem += 1
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        binding.buttonSkip.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
} 