package com.example.blockchainproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blockchainproject.data.local.SharedPrefsHelper
import com.example.blockchainproject.repository.AccountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AccountRepository = AccountRepository(),
    private val sharedPrefs: SharedPrefsHelper
) : ViewModel() {

    private val _isLoginSuccessful = MutableStateFlow<Boolean?>(null)
    val isLoginSuccessful: StateFlow<Boolean?> = _isLoginSuccessful

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _shouldLogout = MutableStateFlow(false)
    val shouldLogout: StateFlow<Boolean> = _shouldLogout

    fun checkAccountAndLogin(address: String, save: Boolean = false) {
        _isLoading.value = true
        _isLoginSuccessful.value = null
        viewModelScope.launch {
            val isRealAccount = repository.getAccountDetails(address)
            if (isRealAccount && save) {
                sharedPrefs.saveAddress(address)
            }
            _isLoginSuccessful.value = isRealAccount
            _isLoading.value = false
        }
    }

    fun autoLoginIfPossible(): String? {
        val savedAddress = sharedPrefs.getSavedAddress()
        if (!savedAddress.isNullOrEmpty()) {
            _isLoginSuccessful.value = true
            validateInBackground(savedAddress)
            return savedAddress
        }
        return null
    }

    private fun validateInBackground(address: String) {
        viewModelScope.launch {
            val isValid = repository.getAccountDetails(address)
            if (!isValid) {
                sharedPrefs.clearAddress()
                _shouldLogout.value = true
            }
        }
    }

    fun resetLoginState() {
        _isLoginSuccessful.value = null
    }

    fun acknowledgeLogoutHandled() {
        _shouldLogout.value = false
    }
}
