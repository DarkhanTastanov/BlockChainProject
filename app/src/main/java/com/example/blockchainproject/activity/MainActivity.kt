package com.example.blockchainproject.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import com.example.blockchainproject.data.local.SharedPrefsHelper
import com.example.blockchainproject.repository.AccountRepository
import com.example.blockchainproject.ui.compose.navigation.AppNavigation
import com.example.blockchainproject.ui.compose.screen.SplashScreen
import com.example.blockchainproject.ui.viewmodel.MainUiState
import com.example.blockchainproject.ui.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private var showSplash by mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = AccountRepository(this)
        val sharedPrefs = SharedPrefsHelper(this)
        viewModel = MainViewModel(repository, sharedPrefs)

        setContent {
            val uiState by viewModel.uiState.collectAsState()

            Box {
                if (uiState !is MainUiState.Loading) {
                    AppNavigation(
                        startDestination = when (uiState) {
                            is MainUiState.LoginRequired -> "login"
                            is MainUiState.LoggedIn -> "home"
                            else -> "login"
                        }
                    )
                }

                if (uiState is MainUiState.Loading || showSplash) {
                    SplashScreen {
                        showSplash = false
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (!isFinishing) {
            showSplash = true
        }
    }
}
