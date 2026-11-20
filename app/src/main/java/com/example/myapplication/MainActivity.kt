package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.DashboardScreen
import com.example.myapplication.ui.LoginScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.viewmodel.KAUViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val kauViewModel: KAUViewModel = viewModel()
                if (kauViewModel.currentUser == null) {
                    LoginScreen(viewModel = kauViewModel)
                } else {
                    DashboardScreen(viewModel = kauViewModel)
                }
            }
        }
    }
}
