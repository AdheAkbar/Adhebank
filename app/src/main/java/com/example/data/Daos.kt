package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {
    @Query("SELECT * FROM wallets WHERE isActive = 1 ORDER BY createdAt DESC")
    fun getAllWallets(): Flow<List<WalletEntity>>

    @Insert
    suspend fun insertWallet(wallet: WalletEntity)
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY type ASC, createdAt DESC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Insert
    suspend fun insertCategory(category: CategoryEntity)
    
    @Insert
    suspend fun insertAll(categories: List<CategoryEntity>)
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY dateMillis DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Insert
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'income'")
    fun getTotalIncome(): Flow<Double?>
    
    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'expense'")
    fun getTotalExpense(): Flow<Double?>

    @androidx.room.Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @androidx.room.Update
    suspend fun updateTransaction(transaction: TransactionEntity)
}
