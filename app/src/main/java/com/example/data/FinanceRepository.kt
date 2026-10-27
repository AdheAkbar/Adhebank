package com.example.data

import kotlinx.coroutines.flow.Flow

class FinanceRepository(private val db: FinanceDatabase) {
    val allWallets: Flow<List<WalletEntity>> = db.walletDao().getAllWallets()
    val allCategories: Flow<List<CategoryEntity>> = db.categoryDao().getAllCategories()
    val allTransactions: Flow<List<TransactionEntity>> = db.transactionDao().getAllTransactions()
    
    val totalIncome: Flow<Double?> = db.transactionDao().getTotalIncome()
    val totalExpense: Flow<Double?> = db.transactionDao().getTotalExpense()

    suspend fun insertWallet(wallet: WalletEntity) {
        db.walletDao().insertWallet(wallet)
    }

    suspend fun insertCategory(category: CategoryEntity) {
        db.categoryDao().insertCategory(category)
    }

    suspend fun insertTransaction(transaction: TransactionEntity) {
        db.transactionDao().insertTransaction(transaction)
    }

    suspend fun updateTransaction(transaction: TransactionEntity) {
        db.transactionDao().updateTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) {
        db.transactionDao().deleteTransaction(transaction)
    }
    
    suspend fun seedCategories() {
        val initialCategories = listOf(
            CategoryEntity(name = "Gaji", type = "income", iconName = "briefcase", colorHex = "#10B981"),
            CategoryEntity(name = "Makan & Minum", type = "expense", iconName = "utensils", colorHex = "#F59E0B"),
            CategoryEntity(name = "Transport", type = "expense", iconName = "car", colorHex = "#6366F1"),
            CategoryEntity(name = "Belanja", type = "expense", iconName = "shopping-bag", colorHex = "#EC4899"),
            CategoryEntity(name = "Lainnya", type = "expense", iconName = "more-horizontal", colorHex = "#9CA3AF")
        )
        db.categoryDao().insertAll(initialCategories)
    }
}
