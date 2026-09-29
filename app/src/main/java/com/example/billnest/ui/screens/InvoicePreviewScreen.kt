package com.example.billnest.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billnest.data.model.Invoice
import com.example.billnest.data.model.InvoiceStatus
import com.example.billnest.data.model.TemplateStyle
import com.example.billnest.ui.components.StatusBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoicePreviewScreen(
    invoice: Invoice,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onSendEmail: () -> Unit,
    onUpdateStatus: (InvoiceStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val accentColor = try {
        Color(android.graphics.Color.parseColor(invoice.themeColor))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }

    fun shareInvoiceAsText(ctx: Context, inv: Invoice) {
        val shareBody = buildString {
            appendLine("========================================")
            appendLine("INVOICE: ${inv.invoiceNumber}")
            appendLine("From: ${inv.businessName} (${inv.businessEmail})")
            appendLine("To: ${inv.clientName} (${inv.clientEmail})")
            appendLine("Issue Date: ${inv.issueDate} | Due Date: ${inv.dueDate}")
            appendLine("Status: ${inv.status.name}")
            appendLine("----------------------------------------")
            appendLine("ITEMS:")
            inv.items.forEach { item ->
                appendLine(" - ${item.description}: ${item.quantity} x ${inv.currency} ${item.rate} = ${inv.currency} ${item.amount}")
            }
            appendLine("----------------------------------------")
            appendLine("Subtotal: ${inv.currency} ${inv.subtotal}")
            if (inv.taxAmount > 0) appendLine("Tax (${inv.taxRate}%): ${inv.currency} ${inv.taxAmount}")
            if (inv.discountAmount > 0) appendLine("Discount: -${inv.currency} ${inv.discountAmount}")
            appendLine("TOTAL: ${inv.currency} ${inv.total}")
            if (inv.bankDetails.bankName.isNotBlank() || inv.bankDetails.upiId.isNotBlank()) {
                appendLine("----------------------------------------")
                appendLine("PAYMENT DETAILS:")
                if (inv.bankDetails.bankName.isNotBlank()) appendLine("Bank: ${inv.bankDetails.bankName}")
                if (inv.bankDetails.accountNumber.isNotBlank()) appendLine("Account #: ${inv.bankDetails.accountNumber}")
                if (inv.bankDetails.routingCode.isNotBlank()) appendLine("IFSC/Routing: ${inv.bankDetails.routingCode}")
                if (inv.bankDetails.upiId.isNotBlank()) appendLine("UPI: ${inv.bankDetails.upiId}")
            }
            appendLine("========================================")
            appendLine("Generated via Billnest — Invoices, paid faster")
        }

        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, shareBody)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share Invoice ${inv.invoiceNumber}")
        ctx.startActivity(shareIntent)
    }

    Scaffold(
        modifier = modifier.testTag("invoice_preview_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(invoice.invoiceNumber, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(
                            text = "Template: ${invoice.templateStyle.name}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("preview_back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { shareInvoiceAsText(context, invoice) },
                        modifier = Modifier.testTag("preview_share_button")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.testTag("preview_edit_button")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Quick Status & Action Buttons Banner
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusBadge(status = invoice.status)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (invoice.status != InvoiceStatus.Paid) {
                            Button(
                                onClick = { onUpdateStatus(InvoiceStatus.Paid) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Mark Paid", fontSize = 12.sp)
                            }
                        }
                        Button(
                            onClick = onSendEmail,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("preview_send_email_button")
                        ) {
                            Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Email", fontSize = 12.sp)
                        }
                    }
                }
            }

            // Paper Invoice Sheet
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("invoice_paper_sheet"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        // Header Bar based on Template
                        when (invoice.templateStyle) {
                            TemplateStyle.Modern -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .background(accentColor)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                            TemplateStyle.Classic -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, accentColor)
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("OFFICIAL INVOICE", fontWeight = FontWeight.Bold, color = accentColor, letterSpacing = 2.sp)
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                            TemplateStyle.Minimal -> {
                                // Clean minimal spacing
                            }
                        }

                        // Business Logo & Name
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(accentColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = invoice.businessLogoLetter.ifBlank { "B" },
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = invoice.businessName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        color = Color(0xFF111116)
                                    )
                                    Text(
                                        text = invoice.businessEmail,
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "INVOICE",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 20.sp,
                                    color = accentColor
                                )
                                Text(
                                    text = invoice.invoiceNumber,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF111116)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        Divider(color = Color(0xFFEEEEEE))
                        Spacer(modifier = Modifier.height(16.dp))

                        // Client and Dates Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("BILLED TO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = invoice.clientName.ifBlank { "Client" },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF111116)
                                )
                                if (invoice.clientEmail.isNotBlank()) {
                                    Text(invoice.clientEmail, fontSize = 13.sp, color = Color.Gray)
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("DATES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Issued: ${invoice.issueDate}", fontSize = 13.sp, color = Color(0xFF111116))
                                Text("Due: ${invoice.dueDate}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = accentColor)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Table Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF9FAFB))
                                .padding(horizontal = 8.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("DESCRIPTION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.weight(2f))
                            Text("QTY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.weight(0.7f))
                            Text("RATE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.weight(1f))
                            Text("AMOUNT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.weight(1.2f))
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Items list
                        invoice.items.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(item.description, fontSize = 13.sp, color = Color(0xFF111116), modifier = Modifier.weight(2f))
                                Text(item.quantity.toString().replace(".0", ""), fontSize = 13.sp, color = Color(0xFF111116), modifier = Modifier.weight(0.7f))
                                Text("${invoice.currency} ${item.rate.toInt()}", fontSize = 13.sp, color = Color(0xFF111116), modifier = Modifier.weight(1f))
                                Text(
                                    "${invoice.currency} ${String.format("%.2f", item.amount)}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF111116),
                                    modifier = Modifier.weight(1.2f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(color = Color(0xFFEEEEEE))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Calculations Block
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.End
                        ) {
                            Row(
                                modifier = Modifier.width(220.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Subtotal:", fontSize = 13.sp, color = Color.Gray)
                                Text("${invoice.currency} ${String.format("%.2f", invoice.subtotal)}", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF111116))
                            }
                            if (invoice.taxAmount > 0) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.width(220.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Tax (${invoice.taxRate}%):", fontSize = 13.sp, color = Color.Gray)
                                    Text("+${invoice.currency} ${String.format("%.2f", invoice.taxAmount)}", fontSize = 13.sp, color = Color(0xFF111116))
                                }
                            }
                            if (invoice.discountAmount > 0) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.width(220.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Discount:", fontSize = 13.sp, color = Color.Gray)
                                    Text("-${invoice.currency} ${String.format("%.2f", invoice.discountAmount)}", fontSize = 13.sp, color = accentColor)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Divider(modifier = Modifier.width(220.dp), color = Color(0xFFEEEEEE))
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.width(220.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Total:", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111116))
                                Text(
                                    "${invoice.currency} ${String.format("%.2f", invoice.total)}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = accentColor
                                )
                            }
                        }

                        // Bank / Payment Instructions Card
                        if (invoice.bankDetails.bankName.isNotBlank() || invoice.bankDetails.upiId.isNotBlank() || invoice.notes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(24.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFF9FAFB))
                                    .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(10.dp))
                                    .padding(14.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("PAYMENT DETAILS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = accentColor)
                                    if (invoice.bankDetails.bankName.isNotBlank()) {
                                        Text("Bank: ${invoice.bankDetails.bankName} • Account: ${invoice.bankDetails.accountNumber}", fontSize = 12.sp, color = Color(0xFF111116))
                                    }
                                    if (invoice.bankDetails.routingCode.isNotBlank()) {
                                        Text("IFSC/Routing: ${invoice.bankDetails.routingCode}", fontSize = 12.sp, color = Color(0xFF111116))
                                    }
                                    if (invoice.bankDetails.upiId.isNotBlank()) {
                                        Text("UPI ID: ${invoice.bankDetails.upiId}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF111116))
                                    }
                                    if (invoice.notes.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(invoice.notes, fontSize = 12.sp, color = Color.Gray)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Share & Export Button
            item {
                Button(
                    onClick = { shareInvoiceAsText(context, invoice) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("preview_export_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share & Export Invoice", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
