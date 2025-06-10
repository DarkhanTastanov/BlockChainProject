package com.example.blockchainproject.data.entity

data class AccountRequest(
    val address: String,
    val visible: Boolean = true
)