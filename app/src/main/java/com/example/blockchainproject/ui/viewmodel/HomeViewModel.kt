package com.example.blockchainproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blockchainproject.data.entity.AccountInfo
import com.example.blockchainproject.data.local.SharedPrefsHelper
import com.example.blockchainproject.repository.AccountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: AccountRepository = AccountRepository(),
    private val sharedPrefs: SharedPrefsHelper
) : ViewModel() {

    private val _accountInfo = MutableStateFlow<AccountInfo?>(null)
    val accountInfo: StateFlow<AccountInfo?> = _accountInfo

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadAccountData() {
        val address = sharedPrefs.getSavedAddress() ?: return
        _isLoading.value = true
        viewModelScope.launch {
            _accountInfo.value = repository.fetchAccountInfo(address)
            _isLoading.value = false
        }
    }
}
