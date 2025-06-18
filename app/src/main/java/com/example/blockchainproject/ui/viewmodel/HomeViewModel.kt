package com.example.blockchainproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blockchainproject.data.entity.AccountInfo
import com.example.blockchainproject.data.local.SharedPrefsHelper
import com.example.blockchainproject.repository.AccountRepository
import com.example.blockchainproject.repository.source.NetworkModeManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: AccountRepository,
    private val sharedPrefs: SharedPrefsHelper
) : ViewModel() {

    private val _accountInfo = MutableStateFlow<AccountInfo?>(null)
    val accountInfo: StateFlow<AccountInfo?> = _accountInfo

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    val isMainNet: StateFlow<Boolean> = NetworkModeManager.isMainNet

    init {
        val saved = sharedPrefs.getIsMainNet()
        NetworkModeManager.setNetworkMode(saved)
        viewModelScope.launch {
            NetworkModeManager.isMainNet.collectLatest { isMain ->
                loadCachedData(isMain)
                refreshAccountInfo(isMain)
            }
        }
    }



    private fun loadCachedData(isMainNet: Boolean) {
        _isLoading.value = true
        val cached = sharedPrefs.getSavedAccountInfo(isMainNet)
        _accountInfo.value = cached
    }


    private suspend fun refreshAccountInfo(isMainNet: Boolean) {
        val address = sharedPrefs.getSavedAddress() ?: return
        val freshData = repository.fetchAccountInfo(address, isMainNet)
        // In HomeViewModel's refreshAccountInfo
        if (freshData != null) {
            sharedPrefs.saveAccountInfo(freshData, isMainNet)
            _accountInfo.value = freshData.copy() // If AccountInfo is a data class, use copy() to create a new instance
        }
        _isLoading.value = false
    }

    private fun refreshInBackground() {
        val address = sharedPrefs.getSavedAddress() ?: return
        viewModelScope.launch {
            val currentNet = NetworkModeManager.isMainNet.value
            val freshData = repository.fetchAccountInfo(address, currentNet)
            if (freshData != null) {
                sharedPrefs.saveAccountInfo(freshData, currentNet)
                _accountInfo.value = freshData
            }
            _isLoading.value = false
        }
    }

    fun reloadManually() {
        val address = sharedPrefs.getSavedAddress() ?: return
        _isLoading.value = true
        viewModelScope.launch {
            val currentNet = NetworkModeManager.isMainNet.value
            val freshData = repository.fetchAccountInfo(address, currentNet)
            if (freshData != null) {
                sharedPrefs.saveAccountInfo(freshData, currentNet)
                _accountInfo.value = freshData
            }
            _isLoading.value = false
        }
    }

    fun toggleNetworkMode() {
        val newValue = !NetworkModeManager.isMainNet.value
        NetworkModeManager.setNetworkMode(newValue)
        sharedPrefs.setIsMainNet(newValue)
        refreshInBackground()
    }


}
