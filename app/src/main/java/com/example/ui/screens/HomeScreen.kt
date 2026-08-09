package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CreditEntity
import com.example.data.local.HistoryEntity
import com.example.ui.components.CreditBadge
import com.example.ui.components.ToolCard
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldGlow
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonPurpleLight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    creditEntity: CreditEntity?,
    recentHistory: List<HistoryEntity>,
    onNavigateToChat: () -> Unit,
    onNavigateToPromptGen: () -> Unit,
    onNavigateToEssayWriter: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onOpenBuyCreditsModal: () -> Unit,
    onQuickPromptSelected: (String) -> Unit
) {
    val credits = creditEntity?.credits ?: 5
    val isUnlimited = creditEntity?.isUnlimited == true

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkSurface)
            .padding(horizontal = 18.dp)
            .testTag("home_screen_lazy_column")
    ) {
        // Top Header
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(NeonPurple, NeonCyan)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Tools Hub",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "AI Tools Hub",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Powered by Gemini 3.5",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                CreditBadge(
                    creditEntity = creditEntity,
                    onOpenModal = onOpenBuyCreditsModal
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Status / Credit Banner Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .border(
                        1.dp,
                        Brush.horizontalGradient(listOf(NeonPurple, NeonCyan)),
                        RoundedCornerShape(22.dp)
                    )
                    .clickable { onOpenBuyCreditsModal() },
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF2D1B69).copy(alpha = 0.8f),
                                    Color(0xFF131824)
                                )
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = GoldGlow,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isUnlimited) "Unlimited Pro Activated" else "Daily Free Credits",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(GoldAccent.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (isUnlimited) "PRO" else "$credits / 5 Remaining",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldGlow
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (isUnlimited)
                                "Enjoy unrestricted access to AI Chat, Prompt Engineering & Essay Writing!"
                            else if (credits > 0)
                                "5 Free Credits refreshed every midnight. Tap to add more credits or unlock Unlimited Pro."
                            else
                                "You've used all 5 daily credits! Tap here to refill 20 credits or upgrade to Unlimited Pro.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Section Title: Core AI Tools
        item {
            Text(
                text = "Core AI Suite",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Tool 1: AI Chat
        item {
            ToolCard(
                title = "AI Chat",
                subtitle = "Intelligent multi-turn assistant for instant answers, brainstorming & problem solving.",
                badgeText = "1 Credit / msg",
                icon = Icons.Default.ChatBubbleOutline,
                gradientColors = listOf(NeonPurple, Color(0xFF6366F1)),
                testTag = "home_tool_chat_card",
                onClick = onNavigateToChat
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Tool 2: Prompt Generator
        item {
            ToolCard(
                title = "Prompt Generator",
                subtitle = "Craft supercharged high-yield prompts for ChatGPT, Midjourney, Claude & Gemini.",
                badgeText = "1 Credit",
                icon = Icons.Default.Psychology,
                gradientColors = listOf(NeonCyan, Color(0xFF0284C7)),
                testTag = "home_tool_prompt_gen_card",
                onClick = onNavigateToPromptGen
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Tool 3: Essay Writer
        item {
            ToolCard(
                title = "Essay Writer",
                subtitle = "Compose structured academic essays with thesis, outline, arguments & conclusion.",
                badgeText = "1 Credit",
                icon = Icons.Default.EditNote,
                gradientColors = listOf(Color(0xFFEC4899), Color(0xFF8B5CF6)),
                testTag = "home_tool_essay_writer_card",
                onClick = onNavigateToEssayWriter
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Quick Ideas / Starter Prompt Chips
        item {
            Text(
                text = "Quick Inspiration",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            val starterPrompts = listOf(
                "💡 Brainstorm 5 viral app ideas",
                "📝 Write a professional email to client",
                "🧬 Explain quantum mechanics simply",
                "🎨 Design a Midjourney portrait prompt",
                "📚 Draft a 500-word essay on AI ethics"
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(starterPrompts) { prompt ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(DarkSurfaceVariant)
                            .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
                            .clickable {
                                onQuickPromptSelected(prompt.substringAfter(" "))
                            }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = prompt,
                            fontSize = 12.sp,
                            color = NeonPurpleLight,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // History Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Recent History",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                if (recentHistory.isNotEmpty()) {
                    Text(
                        text = "View All",
                        fontSize = 13.sp,
                        color = NeonCyan,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clickable { onNavigateToHistory() }
                            .padding(4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // History List or Empty Placeholder
        if (recentHistory.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No generations yet",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                        Text(
                            text = "Try AI Chat, Prompt Generator, or Essay Writer above!",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        } else {
            items(recentHistory.take(3)) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onNavigateToHistory() },
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = item.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                maxLines = 1,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF2E3852))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = item.toolType,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonCyan
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.generatedOutput,
                            fontSize = 12.sp,
                            color = TextSecondary,
                            maxLines = 2
                        )
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
