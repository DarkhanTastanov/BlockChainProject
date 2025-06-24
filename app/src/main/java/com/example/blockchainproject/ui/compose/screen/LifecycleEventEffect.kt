package com.example.blockchainproject.ui.compose.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner


@Composable
fun LifecycleEventEffect(
    lifecycleOwner: LifecycleOwner,
    event: Lifecycle.Event,
    onEvent: () -> Unit
) {
    val currentLifecycleOwner = rememberUpdatedState(lifecycleOwner)

    DisposableEffect(currentLifecycleOwner.value) {
        val observer = LifecycleEventObserver { _, e ->
            if (e == event) onEvent()
        }

        val lifecycle = currentLifecycleOwner.value.lifecycle
        lifecycle.addObserver(observer)

        onDispose { lifecycle.removeObserver(observer) }
    }
}
