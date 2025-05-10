package com.example.budgetapp.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.budgetapp.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonReset.setOnClickListener {
            // TODO: Implement password reset logic
            finish()
        }
    }
} 