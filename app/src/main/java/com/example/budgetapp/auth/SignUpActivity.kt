package com.example.budgetapp.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.budgetapp.data.AppDatabase
import com.example.budgetapp.data.UserRepository
import com.example.budgetapp.databinding.ActivitySignupBinding
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize UserRepository with UserDao
        val database = AppDatabase.getDatabase(applicationContext)
        userRepository = UserRepository(database.userDao())

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.buttonSignUp.setOnClickListener {
            val name = binding.editTextName.text.toString()
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            val confirmPassword = binding.editTextConfirmPassword.text.toString()

            if (validateInput(name, email, password, confirmPassword)) {
                registerUser(name, email, password)
            }
        }

        binding.textLogin.setOnClickListener {
            finish() // Go back to login screen
        }
    }

    private fun registerUser(name: String, email: String, password: String) {
        lifecycleScope.launch {
            try {
                binding.buttonSignUp.isEnabled = false
                binding.progressBar.visibility = android.view.View.VISIBLE

                val user = userRepository.registerUser(email, password, name)
                if (user != null) {
                    // Registration successful
                    Toast.makeText(this@SignUpActivity, "Registration successful! Please login.", Toast.LENGTH_SHORT).show()
                    // Navigate to login screen
                    startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
                    finish()
                } else {
                    // Registration failed
                    Toast.makeText(this@SignUpActivity, "Email already exists", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@SignUpActivity, e.message ?: "Registration failed", Toast.LENGTH_SHORT).show()
            } finally {
                binding.buttonSignUp.isEnabled = true
                binding.progressBar.visibility = android.view.View.GONE
            }
        }
    }

    private fun validateInput(name: String, email: String, password: String, confirmPassword: String): Boolean {
        var isValid = true

        if (name.isEmpty()) {
            binding.editTextName.error = "Name is required"
            isValid = false
        }

        if (email.isEmpty()) {
            binding.editTextEmail.error = "Email is required"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.editTextEmail.error = "Invalid email format"
            isValid = false
        }

        if (password.isEmpty()) {
            binding.editTextPassword.error = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            binding.editTextPassword.error = "Password must be at least 6 characters"
            isValid = false
        }

        if (confirmPassword.isEmpty()) {
            binding.editTextConfirmPassword.error = "Please confirm your password"
            isValid = false
        } else if (password != confirmPassword) {
            binding.editTextConfirmPassword.error = "Passwords do not match"
            isValid = false
        }

        return isValid
    }
} 