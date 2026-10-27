package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.FinanceDatabase
import com.example.data.FinanceRepository
import com.example.ui.FinanceApp
import com.example.ui.FinanceViewModel
import com.example.ui.FinanceViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val database = FinanceDatabase.getDatabase(this)
        val repository = FinanceRepository(database)
        val factory = FinanceViewModelFactory(repository)

        setContent {
            MyApplicationTheme {
                val viewModel: FinanceViewModel = viewModel(factory = factory)
                FinanceApp(viewModel = viewModel)
            }
        }
    }
}
