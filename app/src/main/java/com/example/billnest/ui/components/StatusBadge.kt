package com.example.billnest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billnest.data.model.InvoiceStatus
import com.example.billnest.ui.theme.StatusDraft
import com.example.billnest.ui.theme.StatusOverdue
import com.example.billnest.ui.theme.StatusPaid
import com.example.billnest.ui.theme.StatusSent

@Composable
fun StatusBadge(
    status: InvoiceStatus,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (status) {
        InvoiceStatus.Paid -> Pair(StatusPaid.copy(alpha = 0.15f), StatusPaid)
        InvoiceStatus.Sent -> Pair(StatusSent.copy(alpha = 0.15f), StatusSent)
        InvoiceStatus.Draft -> Pair(StatusDraft.copy(alpha = 0.15f), StatusDraft)
        InvoiceStatus.Overdue -> Pair(StatusOverdue.copy(alpha = 0.15f), StatusOverdue)
    }

    Box(
        modifier = modifier
            .testTag("status_badge_${status.name.lowercase()}")
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = status.name,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
