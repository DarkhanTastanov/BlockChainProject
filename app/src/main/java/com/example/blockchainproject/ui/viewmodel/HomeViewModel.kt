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

    init {
        loadCachedData()
        refreshInBackground()
    }

    private fun loadCachedData() {
        val cached = sharedPrefs.getSavedAccountInfo()
        _accountInfo.value = cached
        _isLoading.value = true
    }

    private fun refreshInBackground() {
        val address = sharedPrefs.getSavedAddress() ?: return
        viewModelScope.launch {
            val freshData = repository.fetchAccountInfo(address)
            if (freshData != null && freshData != _accountInfo.value) {
                sharedPrefs.saveAccountInfo(freshData)
                _accountInfo.value = freshData
            }
            _isLoading.value = false

        }
    }

    fun reloadManually() {
        val address = sharedPrefs.getSavedAddress() ?: return
        _isLoading.value = true
        viewModelScope.launch {
            val freshData = repository.fetchAccountInfo(address)
            if (freshData != null) {
                sharedPrefs.saveAccountInfo(freshData)
                _accountInfo.value = freshData
            }
            _isLoading.value = false
        }
    }
}
