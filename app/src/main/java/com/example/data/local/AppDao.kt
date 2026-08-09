package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Credits
    @Query("SELECT * FROM user_credits WHERE id = 1")
    fun getCreditsFlow(): Flow<CreditEntity?>

    @Query("SELECT * FROM user_credits WHERE id = 1")
    suspend fun getCredits(): CreditEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCredits(credits: CreditEntity)

    // History
    @Query("SELECT * FROM tool_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<HistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(item: HistoryEntity)

    @Query("DELETE FROM tool_history WHERE id = :id")
    suspend fun deleteHistory(id: Long)

    @Query("DELETE FROM tool_history")
    suspend fun clearHistory()

    // Chat
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllChatMessages(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages")
    suspend fun clearChatMessages()
}
