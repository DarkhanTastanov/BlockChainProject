package com.example.blockchainproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blockchainproject.data.entity.Transaction
import com.example.blockchainproject.data.local.SharedPrefsHelper
import com.example.blockchainproject.repository.AccountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TransactionDetailsViewModel(
    private val repository: AccountRepository = AccountRepository(),
    private val sharedPrefs: SharedPrefsHelper
) : ViewModel() {

    private val _transaction = MutableStateFlow<Transaction?>(null)
    val transaction: StateFlow<Transaction?> = _transaction

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

//    fun loadTransactionByHash(address: String, hash: String) {
//        viewModelScope.launch {
//            val cached = sharedPrefs.getSavedTransactions()
//            val cachedTx = cached.find { it.hash == hash }
//            if (cachedTx != null) {
//                _transaction.value = cachedTx
//                return@launch
//            }
//
//            _isLoading.value = true
//            val fresh = repository.getTransactions(address)
//            val match = fresh.find { it.hash == hash }
//            if (match != null) {
//                sharedPrefs.saveTransactions(fresh)
//                _transaction.value = match
//            }
//            _isLoading.value = false
//        }
//    }

    private val _transactionUrl = MutableStateFlow<String?>(null)
    val transactionUrl: StateFlow<String?> = _transactionUrl

    fun loadTransactionByHash(address: String, hash: String) {
        viewModelScope.launch {
            val cached = sharedPrefs.getSavedTransactions()
            val cachedTx = cached.find { it.hash == hash }
            if (cachedTx != null) {
                _transaction.value = cachedTx
                _transactionUrl.value = generateTransactionUrl(cachedTx.hash)
                return@launch
            }

            _isLoading.value = true
            val fresh = repository.getTransactions(address)
            val match = fresh.find { it.hash == hash }
            if (match != null) {
                sharedPrefs.saveTransactions(fresh)
                _transaction.value = match
                _transactionUrl.value = generateTransactionUrl(match.hash)
            }
            _isLoading.value = false
        }
    }

    private fun generateTransactionUrl(hash: String): String {
        return "https://nile.tronscan.org/#/transaction/$hash"
    }

}
