package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import com.example.data.TransactionEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(viewModel: FinanceViewModel, onNavigateToEdit: () -> Unit) {
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Semua", "Pemasukan", "Pengeluaran")

    val filteredTransactions = when (selectedTabIndex) {
        1 -> transactions.filter { it.type == "income" }
        2 -> transactions.filter { it.type == "expense" }
        else -> transactions
    }

    Scaffold(
        topBar = { 
            Column {
                TopAppBar(title = { Text("Riwayat Transaksi") })
                TabRow(selectedTabIndex = selectedTabIndex) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(filteredTransactions) { trx ->
                val typeColor = if (trx.type == "income") Color(0xFF10B981) else Color(0xFFEF4444)
                val sign = if (trx.type == "income") "+" else "-"
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(trx.note.ifEmpty { "Tanpa Catatan" }, fontWeight = FontWeight.Bold)
                            Text("$sign${formatRupiah(trx.amount)}", color = typeColor, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(onClick = { 
                                viewModel.editingTransaction = trx
                                onNavigateToEdit()
                            }) {
                                Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = { viewModel.deleteTransaction(trx) }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(viewModel: FinanceViewModel, onNavigateBack: () -> Unit) {
    val wallets by viewModel.wallets.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()

    val editingTx = viewModel.editingTransaction
    val isEditing = editingTx != null

    fun formatAmountStr(input: String): String {
        val digits = input.replace(Regex("[^0-9]"), "")
        if (digits.isEmpty()) return ""
        val parsed = digits.toLongOrNull() ?: return ""
        val df = java.text.DecimalFormat("#,###", java.text.DecimalFormatSymbols.getInstance(java.util.Locale.US))
        return df.format(parsed).replace(',', '.')
    }

    var type by remember { mutableStateOf(editingTx?.type ?: "expense") }
    var amount by remember { mutableStateOf(if (isEditing) formatAmountStr(editingTx?.amount?.toLong()?.toString() ?: "") else "") }
    var note by remember { mutableStateOf(editingTx?.note ?: "") }
    var selectedWalletId by remember { mutableStateOf<Int?>(editingTx?.walletId) }
    var selectedCategoryId by remember { mutableStateOf<Int?>(editingTx?.categoryId) }

    DisposableEffect(Unit) {
        onDispose { viewModel.editingTransaction = null }
    }

    LaunchedEffect(wallets, categories) {
        if (!isEditing) {
            if (selectedWalletId == null && wallets.isNotEmpty()) selectedWalletId = wallets.first().id
            if (selectedCategoryId == null && categories.isNotEmpty()) selectedCategoryId = categories.first { it.type == type }.id
        }
    }

    LaunchedEffect(type) {
        if (!isEditing || editingTx?.type != type) {
            val cat = categories.firstOrNull { it.type == type }
            if (cat != null) {
                selectedCategoryId = cat.id
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Edit Transaksi" else "Tambah Transaksi") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Filled.ArrowBack, "Kembali") }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                FilterChip(selected = type == "income", onClick = { type = "income" }, label = { Text("Pemasukan") })
                FilterChip(selected = type == "expense", onClick = { type = "expense" }, label = { Text("Pengeluaran") })
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = formatAmountStr(it) },
                label = { Text("Jumlah (Rp)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Catatan") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val walletToUse = selectedWalletId ?: return@Button
                    val amountVal = amount.replace(".", "").toDoubleOrNull() ?: 0.0
                    if (amountVal > 0) {
                        if (isEditing && editingTx != null) {
                            viewModel.updateTransaction(
                                editingTx.copy(
                                    walletId = walletToUse,
                                    categoryId = selectedCategoryId,
                                    type = type,
                                    amount = amountVal,
                                    note = note,
                                    dateMillis = editingTx.dateMillis
                                )
                            )
                        } else {
                            viewModel.addTransaction(walletToUse, selectedCategoryId, type, amountVal, note, System.currentTimeMillis())
                        }
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Text("Simpan Transaksi")
            }
        }
    }
}
