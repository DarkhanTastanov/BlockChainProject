package com.example.blockchainproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blockchainproject.data.entity.Transaction
import com.example.blockchainproject.data.local.SharedPrefsHelper
import com.example.blockchainproject.repository.AccountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class HistoryViewModel(
    private val repository: AccountRepository = AccountRepository(),
    private val sharedPrefs: SharedPrefsHelper
) : ViewModel() {

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _filter = MutableStateFlow("all")
    val filter: StateFlow<String> = _filter

    fun loadTransactions() {
        val address = sharedPrefs.getSavedAddress() ?: return
        _isLoading.value = true
        viewModelScope.launch {
            val txs = repository.getTransactions(address)
            _transactions.value = txs
            _isLoading.value = false
        }
    }

    fun setFilter(type: String) {
        _filter.value = type
    }

    fun filteredTransactions(): StateFlow<List<Transaction>> {
        return combine(_transactions, _filter) { txs, filter ->
            when (filter) {
                "incoming" -> txs.filter { it.type == "incoming" }
                "outgoing" -> txs.filter { it.type == "outgoing" }
                else -> txs
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    }
}
