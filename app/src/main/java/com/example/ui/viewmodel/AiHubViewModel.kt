package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.ChatMessageEntity
import com.example.data.local.CreditEntity
import com.example.data.local.HistoryEntity
import com.example.data.repository.AiHubRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AiHubViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AiHubRepository

    val creditsState: StateFlow<CreditEntity?>
    val chatMessagesState: StateFlow<List<ChatMessageEntity>>
    val historyListState: StateFlow<List<HistoryEntity>>

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _showBuyCreditsModal = MutableStateFlow(false)
    val showBuyCreditsModal: StateFlow<Boolean> = _showBuyCreditsModal.asStateFlow()

    private val _userMessageInput = MutableStateFlow("")
    val userMessageInput: StateFlow<String> = _userMessageInput.asStateFlow()

    private val _generatedPromptResult = MutableStateFlow<String?>(null)
    val generatedPromptResult: StateFlow<String?> = _generatedPromptResult.asStateFlow()

    private val _generatedEssayResult = MutableStateFlow<String?>(null)
    val generatedEssayResult: StateFlow<String?> = _generatedEssayResult.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    init {
        val appDao = AppDatabase.getDatabase(application).appDao()
        repository = AiHubRepository(appDao)

        creditsState = repository.creditsFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        chatMessagesState = repository.chatMessagesFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        historyListState = repository.historyFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Initialize daily credits refresh
        viewModelScope.launch {
            repository.checkAndRefreshDailyCredits()
        }
    }

    fun openBuyCreditsModal() {
        _showBuyCreditsModal.value = true
    }

    fun closeBuyCreditsModal() {
        _showBuyCreditsModal.value = false
    }

    fun buyStarterPack() {
        viewModelScope.launch {
            repository.addCredits(20)
            _showBuyCreditsModal.value = false
            _toastMessage.value = "Success! +20 Credits added to your account ⚡"
        }
    }

    fun buyUnlimitedSubscription() {
        viewModelScope.launch {
            repository.setUnlimitedSubscription(true)
            _showBuyCreditsModal.value = false
            _toastMessage.value = "Welcome to Unlimited Pro! Enjoy infinite AI generations 🔥"
        }
    }

    fun claimBonusCredits() {
        viewModelScope.launch {
            repository.addCredits(2)
            _toastMessage.value = "+2 Bonus Credits claimed! 🎁"
        }
    }

    fun setUserMessageInput(text: String) {
        _userMessageInput.value = text
    }

    fun sendChatMessage(text: String? = null) {
        val query = text ?: _userMessageInput.value
        if (query.isBlank() || _isGenerating.value) return

        viewModelScope.launch {
            if (!repository.canConsumeCredit()) {
                _showBuyCreditsModal.value = true
                return@launch
            }

            _isGenerating.value = true
            _userMessageInput.value = ""

            val result = repository.sendChatMessage(query)
            _isGenerating.value = false

            if (result.isFailure) {
                val exception = result.exceptionOrNull()
                if (exception?.message == "NO_CREDITS") {
                    _showBuyCreditsModal.value = true
                } else {
                    _toastMessage.value = "Error generating response: ${exception?.localizedMessage}"
                }
            }
        }
    }

    fun generatePrompt(
        topic: String,
        targetPlatform: String,
        tone: String,
        persona: String
    ) {
        if (topic.isBlank() || _isGenerating.value) return

        viewModelScope.launch {
            if (!repository.canConsumeCredit()) {
                _showBuyCreditsModal.value = true
                return@launch
            }

            _isGenerating.value = true
            _generatedPromptResult.value = null

            val result = repository.generatePrompt(
                topic = topic,
                targetPlatform = targetPlatform,
                tone = tone,
                persona = persona
            )
            _isGenerating.value = false

            if (result.isSuccess) {
                _generatedPromptResult.value = result.getOrNull()
                _toastMessage.value = "Prompt generated & saved to history!"
            } else {
                val exception = result.exceptionOrNull()
                if (exception?.message == "NO_CREDITS") {
                    _showBuyCreditsModal.value = true
                } else {
                    _toastMessage.value = "Generation failed: ${exception?.localizedMessage}"
                }
            }
        }
    }

    fun generateEssay(
        topic: String,
        essayType: String,
        wordCount: String,
        academicLevel: String,
        tone: String
    ) {
        if (topic.isBlank() || _isGenerating.value) return

        viewModelScope.launch {
            if (!repository.canConsumeCredit()) {
                _showBuyCreditsModal.value = true
                return@launch
            }

            _isGenerating.value = true
            _generatedEssayResult.value = null

            val result = repository.generateEssay(
                topic = topic,
                essayType = essayType,
                wordCount = wordCount,
                academicLevel = academicLevel,
                tone = tone
            )
            _isGenerating.value = false

            if (result.isSuccess) {
                _generatedEssayResult.value = result.getOrNull()
                _toastMessage.value = "Essay generated & saved to history!"
            } else {
                val exception = result.exceptionOrNull()
                if (exception?.message == "NO_CREDITS") {
                    _showBuyCreditsModal.value = true
                } else {
                    _toastMessage.value = "Generation failed: ${exception?.localizedMessage}"
                }
            }
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            repository.clearChat()
            _toastMessage.value = "Chat history cleared."
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            repository.deleteHistoryItem(id)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
            _toastMessage.value = "History cleared."
        }
    }

    fun clearToast() {
        _toastMessage.value = null
    }
}
