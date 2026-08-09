package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.BuyCreditsModal
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.EssayWriterScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PromptGeneratorScreen
import com.example.ui.theme.AiHubTheme
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.viewmodel.AiHubViewModel

const val ROUTE_HOME = "home"
const val ROUTE_CHAT = "chat"
const val ROUTE_PROMPT_GEN = "prompt_gen"
const val ROUTE_ESSAY_WRITER = "essay_writer"
const val ROUTE_HISTORY = "history"

class MainActivity : ComponentActivity() {

    private val viewModel: AiHubViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AiHubTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: AiHubViewModel) {
    val navController = rememberNavController()
    val context = LocalContext.current

    val creditsState by viewModel.creditsState.collectAsStateWithLifecycle()
    val historyList by viewModel.historyListState.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessagesState.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val showBuyModal by viewModel.showBuyCreditsModal.collectAsStateWithLifecycle()
    val chatInputText by viewModel.userMessageInput.collectAsStateWithLifecycle()
    val generatedPromptResult by viewModel.generatedPromptResult.collectAsStateWithLifecycle()
    val generatedEssayResult by viewModel.generatedEssayResult.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: ROUTE_HOME

    val currentCredits = creditsState?.credits ?: 5
    val isUnlimited = creditsState?.isUnlimited == true

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurfaceVariant,
                contentColor = TextPrimary,
                tonalElevation = androidx.compose.ui.unit.Dp(8f)
            ) {
                val items = listOf(
                    NavigationItem(ROUTE_HOME, "Hub", Icons.Default.AutoAwesome, "nav_item_hub"),
                    NavigationItem(ROUTE_CHAT, "Chat", Icons.Default.ChatBubbleOutline, "nav_item_chat"),
                    NavigationItem(ROUTE_PROMPT_GEN, "Prompts", Icons.Default.Psychology, "nav_item_prompts"),
                    NavigationItem(ROUTE_ESSAY_WRITER, "Essays", Icons.Default.EditNote, "nav_item_essays"),
                    NavigationItem(ROUTE_HISTORY, "Saved", Icons.Default.History, "nav_item_history")
                )

                items.forEach { item ->
                    val selected = currentRoute == item.route
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = if (selected) NeonPurple else TextMuted
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                fontSize = 11.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                color = if (selected) NeonPurple else TextMuted
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = NeonPurple.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.testTag(item.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkSurface)
        ) {
            NavHost(
                navController = navController,
                startDestination = ROUTE_HOME
            ) {
                composable(ROUTE_HOME) {
                    HomeScreen(
                        creditEntity = creditsState,
                        recentHistory = historyList,
                        onNavigateToChat = { navController.navigate(ROUTE_CHAT) },
                        onNavigateToPromptGen = { navController.navigate(ROUTE_PROMPT_GEN) },
                        onNavigateToEssayWriter = { navController.navigate(ROUTE_ESSAY_WRITER) },
                        onNavigateToHistory = { navController.navigate(ROUTE_HISTORY) },
                        onOpenBuyCreditsModal = { viewModel.openBuyCreditsModal() },
                        onQuickPromptSelected = { prompt ->
                            viewModel.setUserMessageInput(prompt)
                            navController.navigate(ROUTE_CHAT)
                        }
                    )
                }

                composable(ROUTE_CHAT) {
                    ChatScreen(
                        creditEntity = creditsState,
                        messages = chatMessages,
                        inputText = chatInputText,
                        isGenerating = isGenerating,
                        onInputChanged = { viewModel.setUserMessageInput(it) },
                        onSendMessage = { prompt -> viewModel.sendChatMessage(prompt) },
                        onClearChat = { viewModel.clearChat() },
                        onOpenBuyCreditsModal = { viewModel.openBuyCreditsModal() }
                    )
                }

                composable(ROUTE_PROMPT_GEN) {
                    PromptGeneratorScreen(
                        creditEntity = creditsState,
                        isGenerating = isGenerating,
                        generatedResult = generatedPromptResult,
                        onGeneratePrompt = { topic, platform, tone, persona ->
                            viewModel.generatePrompt(topic, platform, tone, persona)
                        },
                        onSendToChat = { prompt ->
                            viewModel.setUserMessageInput(prompt)
                            navController.navigate(ROUTE_CHAT)
                        },
                        onOpenBuyCreditsModal = { viewModel.openBuyCreditsModal() }
                    )
                }

                composable(ROUTE_ESSAY_WRITER) {
                    EssayWriterScreen(
                        creditEntity = creditsState,
                        isGenerating = isGenerating,
                        generatedEssay = generatedEssayResult,
                        onGenerateEssay = { topic, type, wc, level, tone ->
                            viewModel.generateEssay(topic, type, wc, level, tone)
                        },
                        onOpenBuyCreditsModal = { viewModel.openBuyCreditsModal() }
                    )
                }

                composable(ROUTE_HISTORY) {
                    HistoryScreen(
                        historyList = historyList,
                        onDeleteHistoryItem = { id -> viewModel.deleteHistoryItem(id) },
                        onClearAllHistory = { viewModel.clearHistory() }
                    )
                }
            }

            // Buy Credits / Premium Subscription Modal
            if (showBuyModal) {
                BuyCreditsModal(
                    onDismiss = { viewModel.closeBuyCreditsModal() },
                    onBuyStarter = { viewModel.buyStarterPack() },
                    onBuyUnlimited = { viewModel.buyUnlimitedSubscription() },
                    onClaimBonus = { viewModel.claimBonusCredits() },
                    currentCredits = currentCredits,
                    isUnlimited = isUnlimited
                )
            }
        }
    }
}

private data class NavigationItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val testTag: String
)
