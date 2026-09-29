package com.example.billnest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billnest.data.model.BankDetails
import com.example.billnest.data.model.Invoice
import com.example.billnest.data.model.InvoiceItem
import com.example.billnest.data.model.InvoiceStatus
import com.example.billnest.data.model.TemplateStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceEditorScreen(
    invoice: Invoice,
    onSaveInvoice: (Invoice) -> Unit,
    onPreviewInvoice: (Invoice) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var businessName by remember { mutableStateOf(invoice.businessName) }
    var businessEmail by remember { mutableStateOf(invoice.businessEmail) }
    var clientName by remember { mutableStateOf(invoice.clientName) }
    var clientEmail by remember { mutableStateOf(invoice.clientEmail) }
    var invoiceNumber by remember { mutableStateOf(invoice.invoiceNumber) }
    var issueDate by remember { mutableStateOf(invoice.issueDate) }
    var dueDate by remember { mutableStateOf(invoice.dueDate) }
    var currency by remember { mutableStateOf(invoice.currency) }
    var taxRateStr by remember { mutableStateOf(if (invoice.taxRate > 0) invoice.taxRate.toString() else "18") }
    var discountStr by remember { mutableStateOf(if (invoice.discountAmount > 0) invoice.discountAmount.toString() else "0") }
    var notes by remember { mutableStateOf(invoice.notes) }
    var status by remember { mutableStateOf(invoice.status) }
    var themeColor by remember { mutableStateOf(invoice.themeColor) }
    var templateStyle by remember { mutableStateOf(invoice.templateStyle) }

    var bankName by remember { mutableStateOf(invoice.bankDetails.bankName) }
    var accountName by remember { mutableStateOf(invoice.bankDetails.accountName) }
    var accountNumber by remember { mutableStateOf(invoice.bankDetails.accountNumber) }
    var routingCode by remember { mutableStateOf(invoice.bankDetails.routingCode) }
    var upiId by remember { mutableStateOf(invoice.bankDetails.upiId) }
    var paymentInstructions by remember { mutableStateOf(invoice.bankDetails.paymentInstructions) }

    var items by remember {
        mutableStateOf(
            if (invoice.items.isNotEmpty()) invoice.items
            else listOf(InvoiceItem("item-1", "Design & Consulting", 1.0, 5000.0))
        )
    }

    val subtotal = items.sumOf { it.amount }
    val taxRate = taxRateStr.toDoubleOrNull() ?: 0.0
    val discount = discountStr.toDoubleOrNull() ?: 0.0
    val taxAmount = (subtotal * taxRate) / 100.0
    val grandTotal = maxOf(0.0, subtotal + taxAmount - discount)

    fun buildCurrentInvoice(): Invoice {
        return invoice.copy(
            businessName = businessName,
            businessEmail = businessEmail,
            clientName = clientName,
            clientEmail = clientEmail,
            invoiceNumber = invoiceNumber,
            issueDate = issueDate,
            dueDate = dueDate,
            currency = currency,
            taxRate = taxRate,
            discountAmount = discount,
            notes = notes,
            status = status,
            themeColor = themeColor,
            templateStyle = templateStyle,
            items = items,
            bankDetails = BankDetails(
                bankName = bankName,
                accountName = accountName,
                accountNumber = accountNumber,
                routingCode = routingCode,
                upiId = upiId,
                paymentInstructions = paymentInstructions
            )
        )
    }

    val availableColors = listOf(
        Pair("#FF5238", "Coral"),
        Pair("#2563EB", "Blue"),
        Pair("#10B981", "Emerald"),
        Pair("#1E293B", "Slate"),
        Pair("#8B5CF6", "Purple")
    )

    Scaffold(
        modifier = modifier.testTag("invoice_editor_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (clientName.isNotBlank()) "Edit: $clientName" else "Invoice Studio",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("editor_back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { onPreviewInvoice(buildCurrentInvoice()) },
                        modifier = Modifier.testTag("editor_preview_button")
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = "Preview")
                    }
                    Button(
                        onClick = { onSaveInvoice(buildCurrentInvoice()) },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("editor_save_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save")
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
            // Template Style & Theme Color Chooser
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Design & Theme",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Template style chips
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TemplateStyle.values().forEach { style ->
                                FilterChip(
                                    selected = templateStyle == style,
                                    onClick = { templateStyle = style },
                                    label = { Text(style.name) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Accent Color",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            availableColors.forEach { (hex, _) ->
                                val color = Color(android.graphics.Color.parseColor(hex))
                                val isSelected = themeColor.equals(hex, ignoreCase = true)
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .clickable { themeColor = hex }
                                        .then(
                                            if (isSelected) Modifier.border(3.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                            else Modifier
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Client & Invoice Metadata Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Invoice Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = invoiceNumber,
                                onValueChange = { invoiceNumber = it },
                                label = { Text("Invoice #") },
                                modifier = Modifier.weight(1f).testTag("invoice_number_input"),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = currency,
                                onValueChange = { currency = it },
                                label = { Text("Currency") },
                                modifier = Modifier.width(90.dp).testTag("currency_input"),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = issueDate,
                                onValueChange = { issueDate = it },
                                label = { Text("Issue Date") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = dueDate,
                                onValueChange = { dueDate = it },
                                label = { Text("Due Date") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        // Status dropdown/selector
                        Text("Invoice Status", style = MaterialTheme.typography.labelMedium)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            InvoiceStatus.values().forEach { s ->
                                FilterChip(
                                    selected = status == s,
                                    onClick = { status = s },
                                    label = { Text(s.name) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Client Information
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Bill To (Client)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                        OutlinedTextField(
                            value = clientName,
                            onValueChange = { clientName = it },
                            label = { Text("Client Name / Business") },
                            modifier = Modifier.fillMaxWidth().testTag("client_name_input"),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = clientEmail,
                            onValueChange = { clientEmail = it },
                            label = { Text("Client Email") },
                            modifier = Modifier.fillMaxWidth().testTag("client_email_input"),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            // Line Items Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Line Items", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Button(
                                onClick = {
                                    items = items + InvoiceItem("item-" + System.currentTimeMillis(), "New Item", 1.0, 1000.0)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), contentColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Item", fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        items.forEachIndexed { index, item ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = item.description,
                                        onValueChange = { newDesc ->
                                            items = items.toMutableList().also { list ->
                                                list[index] = item.copy(description = newDesc)
                                            }
                                        },
                                        label = { Text("Description") },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    if (items.size > 1) {
                                        IconButton(onClick = {
                                            items = items.toMutableList().also { it.removeAt(index) }
                                        }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete item", tint = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = item.quantity.toString().replace(".0", ""),
                                        onValueChange = { qStr ->
                                            val q = qStr.toDoubleOrNull() ?: 1.0
                                            items = items.toMutableList().also { list ->
                                                list[index] = item.copy(quantity = q)
                                            }
                                        },
                                        label = { Text("Qty") },
                                        modifier = Modifier.weight(1f),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true,
                                        shape = RoundedCornerShape(10.dp)
                                    )

                                    OutlinedTextField(
                                        value = item.rate.toString().replace(".0", ""),
                                        onValueChange = { rStr ->
                                            val r = rStr.toDoubleOrNull() ?: 0.0
                                            items = items.toMutableList().also { list ->
                                                list[index] = item.copy(rate = r)
                                            }
                                        },
                                        label = { Text("Rate") },
                                        modifier = Modifier.weight(1.5f),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                        singleLine = true,
                                        shape = RoundedCornerShape(10.dp)
                                    )

                                    Column(
                                        modifier = Modifier.weight(1.2f),
                                        horizontalAlignment = Alignment.End
                                    ) {
                                        Text("Amount", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                        Text(
                                            "$currency ${String.format("%.2f", item.amount)}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                                Divider(modifier = Modifier.padding(top = 12.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                            }
                        }
                    }
                }
            }

            // Tax, Discount & Totals Summary
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Summary & Calculations", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = taxRateStr,
                                onValueChange = { taxRateStr = it },
                                label = { Text("Tax Rate (%)") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = discountStr,
                                onValueChange = { discountStr = it },
                                label = { Text("Discount ($currency)") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )
                        }

                        Divider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Subtotal", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                            Text("$currency ${String.format("%.2f", subtotal)}", fontWeight = FontWeight.Medium)
                        }
                        if (taxAmount > 0) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Tax ($taxRate%)", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                                Text("+$currency ${String.format("%.2f", taxAmount)}", color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                        if (discount > 0) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Discount", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                                Text("-$currency ${String.format("%.2f", discount)}", color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        Divider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Grand Total", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(
                                "$currency ${String.format("%.2f", grandTotal)}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Bank & Payment Details
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Bank & Payment Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                        OutlinedTextField(
                            value = bankName,
                            onValueChange = { bankName = it },
                            label = { Text("Bank Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = accountNumber,
                            onValueChange = { accountNumber = it },
                            label = { Text("Account Number") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = routingCode,
                                onValueChange = { routingCode = it },
                                label = { Text("IFSC / Routing / SWIFT") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = upiId,
                                onValueChange = { upiId = it },
                                label = { Text("UPI ID (VPA)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Notes / Payment Terms") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3,
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }

            // Bottom Actions Row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { onPreviewInvoice(buildCurrentInvoice()) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Preview")
                    }
                    Button(
                        onClick = { onSaveInvoice(buildCurrentInvoice()) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save Invoice")
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
