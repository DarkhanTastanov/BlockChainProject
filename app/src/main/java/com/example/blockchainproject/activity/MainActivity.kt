package com.example.blockchainproject.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.blockchainproject.ui.compose.navigation.AppNavigation
import com.example.blockchainproject.ui.compose.screen.SplashScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val _showCustomSplash = mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val showCustomSplash by remember { _showCustomSplash }

            if (showCustomSplash) {
                SplashScreen {
                    _showCustomSplash.value = false
                }
            } else {
                AppNavigation()
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                _showCustomSplash.value = true
                delay(500)
                _showCustomSplash.value = false
            }
        }
    }

    override fun onPause() {
        super.onPause()
        _showCustomSplash.value = true
    }
}