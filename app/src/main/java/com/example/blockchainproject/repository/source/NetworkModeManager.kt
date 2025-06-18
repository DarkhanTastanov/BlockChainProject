package com.example.blockchainproject.repository.source

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object NetworkModeManager {
    private val _isMainNet = MutableStateFlow(true)
    val isMainNet: StateFlow<Boolean> = _isMainNet

    fun setNetworkMode(value: Boolean) {
        _isMainNet.value = value
    }

}
