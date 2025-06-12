package com.example.blockchainproject.ui.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.blockchainproject.data.local.SharedPrefsHelper
import com.example.blockchainproject.repository.AccountRepository
import com.example.blockchainproject.ui.viewmodel.TransactionDetailsViewModel

class TransactionDetailsViewModelFactory(
    private val repository: AccountRepository,
    private val sharedPrefs: SharedPrefsHelper
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TransactionDetailsViewModel(repository, sharedPrefs) as T
    }
}
