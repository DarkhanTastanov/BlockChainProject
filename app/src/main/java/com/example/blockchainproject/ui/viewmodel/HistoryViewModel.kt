package com.example.blockchainproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blockchainproject.data.entity.Transaction
import com.example.blockchainproject.data.entity.TransactionEntity
import com.example.blockchainproject.data.local.SharedPrefsHelper
import com.example.blockchainproject.repository.AccountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class HistoryViewModel(
    private val repository: AccountRepository,
    private val sharedPrefs: SharedPrefsHelper
) : ViewModel() {

    private val _transactions = MutableStateFlow<List<TransactionEntity>>(emptyList())
    val transactions: StateFlow<List<TransactionEntity>> = _transactions

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _filter = MutableStateFlow("all")
    val filter: StateFlow<String> = _filter

    init {
        loadLocalTransactions()
        refreshTransactions()
    }

    private fun loadLocalTransactions() {
        viewModelScope.launch {
            _isLoading.value = true
            _transactions.value = repository.getLocalTransactions()
            _isLoading.value = false
        }
    }

    private fun refreshTransactions() {
        val address = sharedPrefs.getSavedAddress() ?: return

        viewModelScope.launch {
            _isLoading.value = true
            val fresh = repository.getTransactions(address)
            _transactions.value = fresh
            _isLoading.value = false
        }
    }

    fun loadTransactions() {
        refreshTransactions()
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
