package com.example.blockchainproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blockchainproject.data.entity.Transaction
import com.example.blockchainproject.repository.AccountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TransactionDetailsViewModel(
    private val repository: AccountRepository = AccountRepository()
) : ViewModel() {

    private val _transaction = MutableStateFlow<Transaction?>(null)
    val transaction: StateFlow<Transaction?> = _transaction

    fun loadTransactionByHash(address: String, hash: String) {
        viewModelScope.launch {
            val allTransactions = repository.getTransactions(address)
            _transaction.value = allTransactions.find { it.hash == hash }
        }
    }
}
