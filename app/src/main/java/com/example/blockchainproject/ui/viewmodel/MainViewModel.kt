package com.example.blockchainproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blockchainproject.data.local.SharedPrefsHelper
import com.example.blockchainproject.repository.AccountRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: AccountRepository,
    private val sharedPrefs: SharedPrefsHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val uiState: StateFlow<MainUiState> = _uiState

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        viewModelScope.launch {
            val savedAddress = sharedPrefs.getSavedAddress()
            if (savedAddress.isNullOrEmpty()) {
                _uiState.value = MainUiState.LoginRequired
            } else {
                val isValid = repository.getAccountDetails(savedAddress)
                if (isValid) {
                    _uiState.value = MainUiState.LoggedIn
                } else {
                    sharedPrefs.clearAll()
                    _uiState.value = MainUiState.LoginRequired
                }
            }
        }
    }
}

sealed class MainUiState {
    object Loading : MainUiState()
    object LoggedIn : MainUiState()
    object LoginRequired : MainUiState()
}
