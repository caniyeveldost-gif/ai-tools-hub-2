package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_credits")
data class CreditEntity(
    @PrimaryKey val id: Int = 1,
    val credits: Int = 5,
    val isUnlimited: Boolean = false,
    val lastDailyResetDate: String = "",
    val totalUsed: Int = 0
)
