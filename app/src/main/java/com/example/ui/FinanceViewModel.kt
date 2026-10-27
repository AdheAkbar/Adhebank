package com.example.ui

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.CategoryEntity
import com.example.data.FinanceRepository
import com.example.data.TransactionEntity
import com.example.data.WalletEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FinanceViewModel(private val repository: FinanceRepository) : ViewModel() {

    val wallets: StateFlow<List<WalletEntity>> = repository.allWallets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<CategoryEntity>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val transactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalIncome: StateFlow<Double?> = repository.totalIncome
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalExpense: StateFlow<Double?> = repository.totalExpense
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    init {
        viewModelScope.launch {
            // Setup simple initial seed data if empty
            try {
                repository.seedCategories()
            } catch (e: Exception) {
               // Ignore if already seeded or constraint fails
            }
        }
    }

    fun addWallet(name: String, type: String = "cash", balance: Double = 0.0, colorHex: String = "#3B82F6") {
        viewModelScope.launch {
            repository.insertWallet(WalletEntity(name = name, type = type, initialBalance = balance, colorHex = colorHex))
        }
    }

    fun addTransaction(walletId: Int, categoryId: Int?, type: String, amount: Double, note: String, dateMillis: Long) {
        viewModelScope.launch {
            repository.insertTransaction(
                TransactionEntity(walletId = walletId, categoryId = categoryId, type = type, amount = amount, note = note, dateMillis = dateMillis)
            )
        }
    }

    var editingTransaction by mutableStateOf<TransactionEntity?>(null)

    fun updateTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.updateTransaction(transaction)
            editingTransaction = null
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }
}

class FinanceViewModelFactory(private val repository: FinanceRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FinanceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FinanceViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
