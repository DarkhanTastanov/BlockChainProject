package com.example.blockchainproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blockchainproject.data.entity.TransactionEntity
import com.example.blockchainproject.data.local.SharedPrefsHelper
import com.example.blockchainproject.repository.AccountRepository
import com.example.blockchainproject.repository.source.NetworkModeManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val repository: AccountRepository,
    private val sharedPrefs: SharedPrefsHelper
) : ViewModel() {

    private val _transactions = MutableStateFlow<List<TransactionEntity>>(emptyList())
    val transactions: StateFlow<List<TransactionEntity>> = _transactions

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _filter = MutableStateFlow("all")
    val filter: StateFlow<String> = _filter

    val isMainNet: StateFlow<Boolean> = NetworkModeManager.isMainNet

    init {
        observeNetworkChanges()
    }

    private fun observeNetworkChanges() {
        viewModelScope.launch {
            NetworkModeManager.isMainNet.collectLatest { isMain ->
                _isLoading.value = true
                val netType = if (isMain) "mainnet" else "testnet"

                val address = sharedPrefs.getSavedAddress()
                if (address != null) {
                    val remote = repository.getTransactions(address, isMain)
                    _transactions.value = remote.toList()
                } else {
                    val local = repository.getLocalTransactionsByNetwork(netType)
                    _transactions.value = local.toList()
                }

                _isLoading.value = false
            }
        }
    }

    fun loadTransactions() {
        viewModelScope.launch {
            _isLoading.value = true
            val currentNet = isMainNet.value
            try {
                val address = sharedPrefs.getSavedAddress() ?: return@launch
                val fresh = repository.getTransactions(address, currentNet)
                _transactions.value = fresh.toList()
            } catch (e: Exception) {

            } finally {
                _isLoading.value = false
            }

        }
    }

    fun setFilter(type: String) {
        _filter.value = type
    }

    fun filteredTransactions(): StateFlow<List<TransactionEntity>> {
        return combine(transactions, filter) { list, f ->
            when (f) {
                "incoming" -> list.filter { it.type == "incoming" }
                "outgoing" -> list.filter { it.type == "outgoing" }
                else -> list
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    }
}
