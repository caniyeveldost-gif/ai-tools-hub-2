package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EditNote
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
fun EssayWriterScreen(
    creditEntity: CreditEntity?,
    isGenerating: Boolean,
    generatedEssay: String?,
    onGenerateEssay: (topic: String, essayType: String, wordCount: String, academicLevel: String, tone: String) -> Unit,
    onOpenBuyCreditsModal: () -> Unit
) {
    var topicInput by remember { mutableStateOf("") }
    var selectedEssayType by remember { mutableStateOf("Argumentative") }
    var selectedWordCount by remember { mutableStateOf("Medium (~600w)") }
    var selectedAcademicLevel by remember { mutableStateOf("College") }
    var selectedTone by remember { mutableStateOf("Academic") }

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    val essayTypes = listOf("Argumentative", "Persuasive", "Expository", "Analytical", "Narrative")
    val wordCounts = listOf("Short (~300w)", "Medium (~600w)", "Long (~1000w)")
    val academicLevels = listOf("High School", "College", "Masters / PhD", "Professional")
    val tones = listOf("Academic", "Formal", "Critical", "Engaging")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkSurface)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp)
            .testTag("essay_writer_screen_column")
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Header
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
                        .background(Brush.horizontalGradient(listOf(Color(0xFFEC4899), NeonPurple))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EditNote,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Essay Writer",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Structured academic papers (1 Credit)",
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

        // Input 1: Topic
        Text(
            text = "1. Essay Topic / Research Question",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = topicInput,
            onValueChange = { topicInput = it },
            placeholder = { Text("e.g. The ethical impact of artificial intelligence on employment", fontSize = 13.sp, color = TextMuted) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("essay_writer_topic_input"),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = DarkSurfaceVariant,
                unfocusedContainerColor = DarkSurfaceVariant,
                focusedBorderColor = Color(0xFFEC4899),
                unfocusedBorderColor = DarkBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            minLines = 2,
            maxLines = 4
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Input 2: Essay Type
        Text(
            text = "2. Essay Type",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            essayTypes.forEach { type ->
                val isSelected = selectedEssayType == type
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Color(0xFFEC4899) else DarkSurfaceVariant)
                        .border(1.dp, if (isSelected) Color(0xFFEC4899) else DarkBorder, RoundedCornerShape(12.dp))
                        .clickable { selectedEssayType = type }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = type,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Input 3: Length & Academic Level
        Text(
            text = "3. Target Length & Academic Level",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            wordCounts.forEach { wc ->
                val isSelected = selectedWordCount == wc
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) NeonPurple else DarkSurfaceVariant)
                        .border(1.dp, if (isSelected) NeonPurple else DarkBorder, RoundedCornerShape(12.dp))
                        .clickable { selectedWordCount = wc }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = wc,
                        fontSize = 11.sp,
                        color = if (isSelected) Color.White else TextMuted
                    )
                }
            }

            academicLevels.forEach { level ->
                val isSelected = selectedAcademicLevel == level
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) NeonCyan else DarkSurfaceVariant)
                        .border(1.dp, if (isSelected) NeonCyan else DarkBorder, RoundedCornerShape(12.dp))
                        .clickable { selectedAcademicLevel = level }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = level,
                        fontSize = 11.sp,
                        color = if (isSelected) Color.Black else TextMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Action Button: Compose Essay
        Button(
            onClick = {
                onGenerateEssay(topicInput, selectedEssayType, selectedWordCount, selectedAcademicLevel, selectedTone)
            },
            enabled = topicInput.isNotBlank() && !isGenerating,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("generate_essay_submit_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFEC4899),
                disabledContainerColor = DarkBorder
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            if (isGenerating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text("Composing Essay...", color = Color.White, fontWeight = FontWeight.Bold)
            } else {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Compose Complete Essay (1 Credit)", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Result Card
        if (!generatedEssay.isNullOrBlank()) {
            val wordsCount = generatedEssay.split("\\s+".toRegex()).size

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Generated Essay Result",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFEC4899)
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF2E3852))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "~$wordsCount Words",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonPurpleLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, Brush.horizontalGradient(listOf(Color(0xFFEC4899), NeonPurple)), RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    SelectionContainer {
                        Text(
                            text = generatedEssay,
                            fontSize = 13.sp,
                            color = TextPrimary,
                            lineHeight = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(generatedEssay))
                            Toast.makeText(context, "Copied essay to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E3852)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            tint = Color(0xFFEC4899),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy Full Essay", color = Color(0xFFEC4899), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
