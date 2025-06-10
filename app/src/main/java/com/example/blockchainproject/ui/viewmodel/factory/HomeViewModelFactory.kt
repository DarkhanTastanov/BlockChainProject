package com.example.blockchainproject.ui.viewmodel.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.blockchainproject.data.local.SharedPrefsHelper
import com.example.blockchainproject.ui.viewmodel.HomeViewModel

class HomeViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val prefsHelper = SharedPrefsHelper(context)
        return HomeViewModel(sharedPrefs = prefsHelper) as T
    }
}
