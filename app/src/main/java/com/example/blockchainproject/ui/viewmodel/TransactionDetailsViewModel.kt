package com.example.blockchainproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blockchainproject.data.entity.TransactionEntity
import com.example.blockchainproject.data.local.SharedPrefsHelper
import com.example.blockchainproject.repository.AccountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TransactionDetailsViewModel(
    private val repository: AccountRepository,
    private val sharedPrefs: SharedPrefsHelper
) : ViewModel() {

    private val _transaction = MutableStateFlow<TransactionEntity?>(null)
    val transaction: StateFlow<TransactionEntity?> = _transaction

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _transactionUrl = MutableStateFlow<String?>(null)
    val transactionUrl: StateFlow<String?> = _transactionUrl

    private val _isMainNet = MutableStateFlow(sharedPrefs.getIsMainNet())

    fun loadTransactionByHash(address: String, hash: String) {
        viewModelScope.launch {
            _isLoading.value = true

            val localTransactions = repository.getLocalTransactions()
            val localMatch = localTransactions.find { it.hash == hash }
            if (localMatch != null) {
                _transaction.value = localMatch
                _transactionUrl.value = generateTransactionUrl(localMatch.hash)
                _isLoading.value = false
                return@launch
            }

            val freshTransactions = repository.getTransactions(address, _isMainNet.value)
            val match = freshTransactions.find { it.hash == hash }
            if (match != null) {
                _transaction.value = match
                _transactionUrl.value = generateTransactionUrl(match.hash)
            }

            _isLoading.value = false
        }
    }

    private fun generateTransactionUrl(hash: String): String {
        return if (_isMainNet.value) {
            "https://tronscan.org/#/transaction/$hash"
        } else {
            "https://nile.tronscan.org/#/transaction/$hash"
        }
    }

}
