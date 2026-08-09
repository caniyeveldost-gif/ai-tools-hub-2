package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tool_history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val toolType: String, // "CHAT", "PROMPT", "ESSAY"
    val title: String,
    val promptInput: String,
    val generatedOutput: String,
    val timestamp: Long = System.currentTimeMillis()
)
