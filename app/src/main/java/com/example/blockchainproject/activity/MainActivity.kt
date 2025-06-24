package com.example.blockchainproject.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.blockchainproject.ui.compose.navigation.AppNavigation
import com.example.blockchainproject.ui.compose.screen.LifecycleEventEffect
import com.example.blockchainproject.ui.compose.screen.SplashScreen

class MainActivity : AppCompatActivity() {

    // A flag to track if the app is being launched for the first time in this process lifecycle
    // or if it's resuming from a background state where the process was kept alive.
    // This needs to be a member variable of the Activity, not a Composable state,
    // because Composable states are tied to recomposition, but we need to know
    // this across the Activity's lifecycle.
    private var isFirstLaunchInProcess = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // This state now controls if the splash screen is shown at all.
            // It will be true only on a fresh onCreate (cold start / process recreation).
            // It will be set to false after the splash finishes, and never set back to true
            // if the app simply resumes from background without process death.
            var showSplashOnCreate by remember { mutableStateOf(isFirstLaunchInProcess) }

            // Reset the flag after its initial use in setContent,
            // so subsequent ON_START events don't mistakenly re-trigger the splash.
            DisposableEffect(Unit) {
                onDispose {
                    isFirstLaunchInProcess = false // Set to false after initial composition
                }
            }


            // REMOVE LifecycleEventEffect(ProcessLifecycleOwner.get(), Lifecycle.Event.ON_START)
            // This was the part that always re-triggered the splash on resume.
            // We no longer want to force the splash on ON_START.

            if (showSplashOnCreate) {
                SplashScreen {
                    showSplashOnCreate = false
                    // After splash, AppNavigation will take over.
                    // If you have a deep link to resume to a specific page,
                    // that logic would be handled within AppNavigation's initial route determination.
                }
            } else {
                // When showSplashOnCreate is false (either after initial splash,
                // or on subsequent resume from background), show AppNavigation directly.
                AppNavigation()
            }
        }
    }

    // You might want to consider overriding onStop and onStart to log
    // and observe the lifecycle events if you're still debugging.
    /*
    override fun onStart() {
        super.onStart()
        Log.d("MainActivity", "onStart called. isFirstLaunchInProcess: $isFirstLaunchInProcess")
    }

    override fun onStop() {
        super.onStop()
        Log.d("MainActivity", "onStop called.")
    }
    */
}
