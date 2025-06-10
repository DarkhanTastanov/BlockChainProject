package com.example.blockchainproject.ui.viewmodel.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.blockchainproject.data.local.SharedPrefsHelper
import com.example.blockchainproject.ui.viewmodel.HistoryViewModel

class HistoryViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val sharedPrefsHelper = SharedPrefsHelper(context)
        return HistoryViewModel(sharedPrefs = sharedPrefsHelper) as T
    }
}
