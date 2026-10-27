package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallets")
data class WalletEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: String = "cash",
    val initialBalance: Double = 0.0,
    val colorHex: String = "#3B82F6",
    val iconName: String = "wallet",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: String, // 'income' or 'expense'
    val iconName: String = "tag",
    val colorHex: String = "#6B7280",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val walletId: Int, // FK to Wallet (nullable if it's external, but we'll keep required)
    val categoryId: Int?, // Optional if it's a transfer
    val type: String, // 'income', 'expense', 'transfer'
    val amount: Double,
    val note: String,
    val dateMillis: Long,
    val toWalletId: Int? = null, // For transfers
    val createdAt: Long = System.currentTimeMillis()
)
