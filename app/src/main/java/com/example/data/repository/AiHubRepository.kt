package com.example.data.repository

import com.example.data.api.ContentData
import com.example.data.api.GeminiService
import com.example.data.api.PartData
import com.example.data.local.AppDao
import com.example.data.local.ChatMessageEntity
import com.example.data.local.CreditEntity
import com.example.data.local.HistoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AiHubRepository(
    private val appDao: AppDao,
    private val geminiService: GeminiService = GeminiService()
) {
    val creditsFlow: Flow<CreditEntity?> = appDao.getCreditsFlow()
    val historyFlow: Flow<List<HistoryEntity>> = appDao.getAllHistory()
    val chatMessagesFlow: Flow<List<ChatMessageEntity>> = appDao.getAllChatMessages()

    private fun getTodayDateString(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(Date())
    }

    suspend fun checkAndRefreshDailyCredits(): CreditEntity {
        val today = getTodayDateString()
        var current = appDao.getCredits()
        
        if (current == null) {
            current = CreditEntity(
                id = 1,
                credits = 5,
                isUnlimited = false,
                lastDailyResetDate = today,
                totalUsed = 0
            )
            appDao.saveCredits(current)
            return current
        }

        if (current.lastDailyResetDate != today && !current.isUnlimited) {
            // New day! Reset daily free credits to at least 5 (or preserve remaining if higher)
            val newCredits = maxOf(current.credits, 5)
            val updated = current.copy(
                credits = newCredits,
                lastDailyResetDate = today
            )
            appDao.saveCredits(updated)
            return updated
        }

        return current
    }

    suspend fun canConsumeCredit(): Boolean {
        val current = checkAndRefreshDailyCredits()
        return current.isUnlimited || current.credits > 0
    }

    suspend fun consumeCredit(): Boolean {
        val current = checkAndRefreshDailyCredits()
        if (current.isUnlimited) {
            appDao.saveCredits(current.copy(totalUsed = current.totalUsed + 1))
            return true
        }

        if (current.credits > 0) {
            val updated = current.copy(
                credits = current.credits - 1,
                totalUsed = current.totalUsed + 1
            )
            appDao.saveCredits(updated)
            return true
        }

        return false
    }

    suspend fun addCredits(amount: Int) {
        val current = checkAndRefreshDailyCredits()
        val updated = current.copy(credits = current.credits + amount)
        appDao.saveCredits(updated)
    }

    suspend fun setUnlimitedSubscription(enabled: Boolean) {
        val current = checkAndRefreshDailyCredits()
        val updated = current.copy(isUnlimited = enabled)
        appDao.saveCredits(updated)
    }

    // AI Tool execution methods
    suspend fun sendChatMessage(userText: String): Result<String> {
        if (!canConsumeCredit()) {
            return Result.failure(Exception("NO_CREDITS"))
        }

        // Save user message to chat DB
        appDao.insertChatMessage(ChatMessageEntity(sender = "USER", text = userText))

        // Get past message history for context
        val historyEntities = appDao.getAllChatMessages().firstOrNull() ?: emptyList()
        val formattedHistory = historyEntities.takeLast(10).map {
            ContentData(
                role = if (it.sender == "USER") "user" else "model",
                parts = listOf(PartData(text = it.text))
            )
        }

        val result = geminiService.generateResponse(
            prompt = userText,
            systemInstruction = "You are a helpful, brilliant AI Assistant in the AI Tools Hub app. Answer concisely, formatted with markdown bullet points or bold titles where appropriate.",
            chatHistory = formattedHistory.dropLast(1) // exclude current message since service appends it
        )

        if (result.isSuccess) {
            consumeCredit()
            val aiText = result.getOrNull() ?: ""
            appDao.insertChatMessage(ChatMessageEntity(sender = "AI", text = aiText))
            
            // Also log to history feed
            appDao.insertHistory(
                HistoryEntity(
                    toolType = "CHAT",
                    title = if (userText.length > 30) userText.take(30) + "..." else userText,
                    promptInput = userText,
                    generatedOutput = aiText
                )
            )
        }

        return result
    }

    suspend fun generatePrompt(
        topic: String,
        targetPlatform: String,
        tone: String,
        persona: String
    ): Result<String> {
        if (!canConsumeCredit()) {
            return Result.failure(Exception("NO_CREDITS"))
        }

        val systemInstruction = """
            PROMPT_GENERATOR: You are an expert prompt engineer. Your task is to craft an exceptionally effective, highly optimized prompt tailored for $targetPlatform.
            Tone: $tone
            Persona: $persona
            Structure your response into:
            1. Optimized System/User Prompt
            2. Recommended Negative Constraints / Guidelines
            3. Example Variable Placeholders
        """.trimIndent()

        val prompt = "Craft a professional high-yield prompt for the topic/task: $topic"

        val result = geminiService.generateResponse(
            prompt = prompt,
            systemInstruction = systemInstruction
        )

        if (result.isSuccess) {
            consumeCredit()
            val generatedPrompt = result.getOrNull() ?: ""
            appDao.insertHistory(
                HistoryEntity(
                    toolType = "PROMPT",
                    title = "Prompt for $targetPlatform ($topic)",
                    promptInput = topic,
                    generatedOutput = generatedPrompt
                )
            )
        }

        return result
    }

    suspend fun generateEssay(
        topic: String,
        essayType: String,
        wordCount: String,
        academicLevel: String,
        tone: String
    ): Result<String> {
        if (!canConsumeCredit()) {
            return Result.failure(Exception("NO_CREDITS"))
        }

        val systemInstruction = """
            ESSAY_WRITER: You are a distinguished academic writer and researcher. Compose a complete, high-quality $essayType essay.
            Academic Level: $academicLevel
            Approximate Length: $wordCount
            Tone: $tone
            Format with markdown:
            # Title
            ## Outline
            ---
            ### Introduction (with clear thesis)
            ### Body Paragraphs
            ### Conclusion
        """.trimIndent()

        val prompt = "Write a comprehensive essay on the topic: $topic"

        val result = geminiService.generateResponse(
            prompt = prompt,
            systemInstruction = systemInstruction
        )

        if (result.isSuccess) {
            consumeCredit()
            val essayOutput = result.getOrNull() ?: ""
            appDao.insertHistory(
                HistoryEntity(
                    toolType = "ESSAY",
                    title = "Essay: $topic",
                    promptInput = topic,
                    generatedOutput = essayOutput
                )
            )
        }

        return result
    }

    suspend fun clearChat() {
        appDao.clearChatMessages()
    }

    suspend fun deleteHistoryItem(id: Long) {
        appDao.deleteHistory(id)
    }

    suspend fun clearHistory() {
        appDao.clearHistory()
    }
}
