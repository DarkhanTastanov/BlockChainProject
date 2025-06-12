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

    init {
        loadCachedTransactions()
        refreshInBackground()
    }

    private fun loadCachedTransactions() {
        _transactions.value = sharedPrefs.getSavedTransactions()
        _isLoading.value = false
    }

    private fun refreshInBackground() {
        val address = sharedPrefs.getSavedAddress() ?: return

        val cached = sharedPrefs.getSavedTransactions()
        val hasCache = cached.isNotEmpty()
        _transactions.value = cached

        viewModelScope.launch {
            if (!hasCache) _isLoading.value = true

            val freshData = repository.getTransactions(address)
            if (freshData != _transactions.value) {
                sharedPrefs.saveTransactions(freshData)
                _transactions.value = freshData
            }

            _isLoading.value = false
        }
    }

    fun loadTransactions() {
        refreshInBackground()
    }

    fun setFilter(type: String) {
        _filter.value = type
    }

    fun filteredTransactions(): StateFlow<List<Transaction>> {
        return combine(transactions, filter) { list, f ->
            when (f) {
                "incoming" -> list.filter { it.type == "incoming" }
                "outgoing" -> list.filter { it.type == "outgoing" }
                else -> list
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    }
}
