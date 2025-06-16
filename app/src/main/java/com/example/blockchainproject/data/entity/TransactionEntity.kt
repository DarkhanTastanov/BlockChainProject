package com.example.blockchainproject.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val hash: String,
    val amount: Long,
    val type: String,
    val timestamp: Long,
    val toAddress: String,
    val fromAddress: String
)