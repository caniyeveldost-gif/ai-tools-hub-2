package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CreditEntity
import com.example.ui.components.CreditBadge
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GoldGlow
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonPurpleLight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PromptGeneratorScreen(
    creditEntity: CreditEntity?,
    isGenerating: Boolean,
    generatedResult: String?,
    onGeneratePrompt: (topic: String, targetPlatform: String, tone: String, persona: String) -> Unit,
    onSendToChat: (String) -> Unit,
    onOpenBuyCreditsModal: () -> Unit
) {
    var topicInput by remember { mutableStateOf("") }
    var selectedPlatform by remember { mutableStateOf("ChatGPT") }
    var selectedTone by remember { mutableStateOf("Detailed") }
    var selectedPersona by remember { mutableStateOf("Expert") }

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    val platforms = listOf("ChatGPT", "Gemini", "Midjourney", "Claude", "General")
    val tones = listOf("Detailed", "Creative", "Technical", "Concise", "Persuasive")
    val personas = listOf("Expert", "Assistant", "Critic", "Marketer", "Researcher")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkSurface)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp)
            .testTag("prompt_gen_screen_column")
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Brush.horizontalGradient(listOf(NeonCyan, Color(0xFF0284C7)))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Prompt Studio",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Craft high-yield AI prompts (1 Credit)",
                        fontSize = 11.sp,
                        color = GoldGlow
                    )
                }
            }

            CreditBadge(
                creditEntity = creditEntity,
                onOpenModal = onOpenBuyCreditsModal
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Input 1: Topic / Core Task
        Text(
            text = "1. What is your prompt topic or goal?",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = topicInput,
            onValueChange = { topicInput = it },
            placeholder = { Text("e.g. Landing page copy for eco-friendly water bottle", fontSize = 13.sp, color = TextMuted) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("prompt_gen_topic_input"),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = DarkSurfaceVariant,
                unfocusedContainerColor = DarkSurfaceVariant,
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = DarkBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            minLines = 2,
            maxLines = 4
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Input 2: Target Platform
        Text(
            text = "2. Target AI Platform",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            platforms.forEach { platform ->
                val isSelected = selectedPlatform == platform
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) NeonCyan else DarkSurfaceVariant)
                        .border(
                            1.dp,
                            if (isSelected) NeonCyan else DarkBorder,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { selectedPlatform = platform }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = platform,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.Black else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Input 3: Tone & Persona
        Text(
            text = "3. Tone & Persona",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tones.forEach { tone ->
                val isSelected = selectedTone == tone
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) NeonPurple else DarkSurfaceVariant)
                        .border(1.dp, if (isSelected) NeonPurple else DarkBorder, RoundedCornerShape(12.dp))
                        .clickable { selectedTone = tone }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Tone: $tone",
                        fontSize = 11.sp,
                        color = if (isSelected) Color.White else TextMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action Button: Generate Prompt
        Button(
            onClick = {
                onGeneratePrompt(topicInput, selectedPlatform, selectedTone, selectedPersona)
            },
            enabled = topicInput.isNotBlank() && !isGenerating,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("generate_prompt_submit_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = NeonCyan,
                disabledContainerColor = DarkBorder
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            if (isGenerating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = Color.Black,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text("Crafting Supercharged Prompt...", color = Color.Black, fontWeight = FontWeight.Bold)
            } else {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generate Optimized Prompt (1 Credit)", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Output Result Card
        if (!generatedResult.isNullOrBlank()) {
            Text(
                text = "Generated Supercharged Prompt",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = NeonCyan
            )

            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, Brush.horizontalGradient(listOf(NeonCyan, NeonPurple)), RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    SelectionContainer {
                        Text(
                            text = generatedResult,
                            fontSize = 13.sp,
                            color = TextPrimary,
                            lineHeight = 19.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(generatedResult))
                                Toast.makeText(context, "Copied prompt to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E3852)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy Prompt", color = NeonCyan, fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                onSendToChat(generatedResult)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChatBubbleOutline,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Use in Chat", color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
