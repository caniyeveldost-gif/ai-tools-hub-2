package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CreditEntity
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldGlow
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple

@Composable
fun CreditBadge(
    creditEntity: CreditEntity?,
    onOpenModal: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isUnlimited = creditEntity?.isUnlimited == true
    val credits = creditEntity?.credits ?: 5

    val infiniteTransition = rememberInfiniteTransition(label = "badgePulse")
    val scalePulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val backgroundBrush = if (isUnlimited) {
        Brush.horizontalGradient(listOf(NeonPurple, NeonCyan))
    } else if (credits == 0) {
        Brush.horizontalGradient(listOf(Color(0xFF7F1D1D), Color(0xFF991B1B)))
    } else {
        Brush.horizontalGradient(listOf(Color(0xFF1E1B4B), Color(0xFF312E81)))
    }

    val borderBrush = if (isUnlimited) {
        Brush.horizontalGradient(listOf(GoldGlow, GoldAccent))
    } else if (credits == 0) {
        Brush.horizontalGradient(listOf(Color(0xFFEF4444), Color(0xFFF87171)))
    } else {
        Brush.horizontalGradient(listOf(NeonPurple, NeonCyan))
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .scale(if (credits == 0 || isUnlimited) scalePulse else 1f)
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundBrush)
            .border(1.5.dp, borderBrush, RoundedCornerShape(20.dp))
            .clickable { onOpenModal() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag("credit_badge_chip")
    ) {
        if (isUnlimited) {
            Icon(
                imageVector = Icons.Default.AllInclusive,
                contentDescription = "Unlimited Credits",
                tint = GoldGlow,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Unlimited Pro",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        } else {
            Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = "Credits",
                tint = if (credits > 0) GoldGlow else Color(0xFFFCA5A5),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$credits Credits",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (credits > 0) Color.White else Color(0xFFFCA5A5)
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f))
                .padding(2.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Buy Credits",
                tint = Color.White,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}
