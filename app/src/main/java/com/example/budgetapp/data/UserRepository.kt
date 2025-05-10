package com.example.budgetapp.data

import com.example.budgetapp.data.dao.UserDao
import com.example.budgetapp.data.entity.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest

class UserRepository(private val userDao: UserDao) {

    suspend fun registerUser(email: String, password: String, name: String): User? = withContext(Dispatchers.IO) {
        // Check if user already exists
        if (userDao.getUserByEmail(email) != null) {
            return@withContext null
        }

        // Create new user
        val hashedPassword = hashPassword(password)
        val user = User(
            email = email,
            password = hashedPassword,
            name = name
        )

        // Insert user and return
        val userId = userDao.insert(user)
        user.copy(id = userId)
    }

    suspend fun login(email: String, password: String): User? = withContext(Dispatchers.IO) {
        val user = userDao.getUserByEmail(email)
        if (user != null && user.password == hashPassword(password)) {
            user
        } else {
            null
        }
    }

    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
} 