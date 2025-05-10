package com.example.budgetapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.budgetapp.data.dao.UserDao
import com.example.budgetapp.data.dao.TransactionDao
import com.example.budgetapp.data.entity.User
import com.example.budgetapp.data.entity.Transaction
import com.example.budgetapp.data.entity.Category

@Database(
    entities = [User::class, Transaction::class, Category::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private lateinit var appContext: Context

        fun getDatabase(context: Context): AppDatabase {
            appContext = context.applicationContext
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "budget_app_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        fun getAppContext(): Context = appContext
    }
} 