package com.example.blockchainproject.repository.source

import com.example.blockchainproject.data.local.SharedPrefsHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object NetworkModeManager {
    private lateinit var sharedPrefs: SharedPrefsHelper
    private val _isMainNet = MutableStateFlow(true)
    val isMainNet: StateFlow<Boolean> get() = _isMainNet

    fun init(sharedPrefsHelper: SharedPrefsHelper) {
        sharedPrefs = sharedPrefsHelper
        _isMainNet.value = sharedPrefs.getIsMainNet()
    }

    fun setNetworkMode(value: Boolean) {
        _isMainNet.value = value
        sharedPrefs.setIsMainNet(value)
    }
}
