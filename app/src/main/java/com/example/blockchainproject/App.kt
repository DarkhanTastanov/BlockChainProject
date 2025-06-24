package com.example.blockchainproject

import android.app.Application
import com.example.blockchainproject.data.local.SharedPrefsHelper
import com.example.blockchainproject.repository.source.NetworkModeManager

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        val sharedPrefsHelper = SharedPrefsHelper(applicationContext)
        NetworkModeManager.init(sharedPrefsHelper)
    }
}
