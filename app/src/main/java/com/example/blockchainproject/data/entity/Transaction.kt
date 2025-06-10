package com.example.blockchainproject.data.entity

data class Transaction(
    val hash: String,
    val amount: Long,
    val type: String,
    val timestamp: Long
)