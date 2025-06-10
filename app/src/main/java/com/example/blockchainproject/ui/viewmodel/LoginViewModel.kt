package com.example.blockchainproject.ui.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blockchainproject.repository.AccountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: AccountRepository = AccountRepository()) : ViewModel() {

    private val _isLoginSuccessful = MutableStateFlow<Boolean?>(null)
    val isLoginSuccessful: StateFlow<Boolean?> = _isLoginSuccessful

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun checkAccountAndLogin(address: String) {
        _isLoading.value = true
        _isLoginSuccessful.value = null
        viewModelScope.launch {
            val isRealAccount = repository.getAccountDetails(address)
            _isLoginSuccessful.value = isRealAccount
            _isLoading.value = false
        }
    }

    fun resetLoginState() {
        _isLoginSuccessful.value = null
    }
}