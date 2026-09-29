package com.example.billnest.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billnest.data.model.Invoice
import com.example.billnest.data.model.InvoiceStatus
import com.example.billnest.ui.components.InvoiceCard
import com.example.billnest.ui.components.StatCard
import com.example.billnest.ui.theme.StatusDraft
import com.example.billnest.ui.theme.StatusOverdue
import com.example.billnest.ui.theme.StatusPaid
import com.example.billnest.ui.theme.StatusSent
import com.example.billnest.viewmodel.InvoiceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: InvoiceViewModel,
    onCreateInvoice: () -> Unit,
    onSelectInvoice: (Invoice) -> Unit,
    onEditInvoice: (Invoice) -> Unit,
    onSendEmail: (Invoice) -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateAdmin: () -> Unit,
    onNavigateAuth: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allInvoices by viewModel.allInvoices.collectAsState()
    val filteredInvoices by viewModel.filteredInvoices.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedStatus by viewModel.selectedStatusFilter.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    var showSearchField by remember { mutableStateOf(false) }

    // Stats calculations
    val currencySymbol = allInvoices.firstOrNull()?.currency ?: "₹"
    val totalRevenue = allInvoices.sumOf { it.total }
    val paidRevenue = allInvoices.filter { it.status == InvoiceStatus.Paid }.sumOf { it.total }
    val pendingRevenue = allInvoices.filter { it.status == InvoiceStatus.Sent || it.status == InvoiceStatus.Draft }.sumOf { it.total }
    val overdueCount = allInvoices.count { it.status == InvoiceStatus.Overdue }

    Scaffold(
        modifier = modifier.testTag("dashboard_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "B",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Billnest",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = userProfile.businessName.ifBlank { "Invoice Dashboard" },
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showSearchField = !showSearchField },
                        modifier = Modifier.testTag("dashboard_search_button")
                    ) {
                        Icon(
                            imageVector = if (showSearchField) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = "Search invoices"
                        )
                    }
                    IconButton(
                        onClick = onNavigateAuth,
                        modifier = Modifier.testTag("dashboard_profile_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userProfile.name.firstOrNull()?.uppercase() ?: "U",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 14.sp
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateInvoice,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier.testTag("create_invoice_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create Invoice")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("New Invoice", fontWeight = FontWeight.SemiBold)
                }
            }
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
            // Search field
            if (showSearchField) {
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.searchQuery.value = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_invoices_input"),
                        placeholder = { Text("Search by client name, invoice number, or email...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Stat Cards Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Total Billed",
                        value = "$currencySymbol ${String.format("%.0f", totalRevenue)}",
                        subtitle = "${allInvoices.size} invoices",
                        icon = Icons.Default.ReceiptLong,
                        iconTint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Collected",
                        value = "$currencySymbol ${String.format("%.0f", paidRevenue)}",
                        subtitle = "Fully settled",
                        icon = Icons.Default.CheckCircle,
                        iconTint = StatusPaid,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Pending",
                        value = "$currencySymbol ${String.format("%.0f", pendingRevenue)}",
                        subtitle = "Awaiting payment",
                        icon = Icons.Default.HourglassEmpty,
                        iconTint = StatusSent,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Overdue",
                        value = "$overdueCount",
                        subtitle = if (overdueCount > 0) "Action required" else "All up to date",
                        icon = Icons.Default.Warning,
                        iconTint = StatusOverdue,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Filter Chips
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recent Invoices",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${filteredInvoices.size} found",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = selectedStatus == null,
                                onClick = { viewModel.selectedStatusFilter.value = null },
                                label = { Text("All") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                        items(InvoiceStatus.values()) { status ->
                            FilterChip(
                                selected = selectedStatus == status,
                                onClick = {
                                    viewModel.selectedStatusFilter.value =
                                        if (selectedStatus == status) null else status
                                },
                                label = { Text(status.name) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = when (status) {
                                        InvoiceStatus.Paid -> StatusPaid
                                        InvoiceStatus.Sent -> StatusSent
                                        InvoiceStatus.Draft -> StatusDraft
                                        InvoiceStatus.Overdue -> StatusOverdue
                                    },
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Empty state or Invoice list
            if (filteredInvoices.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (searchQuery.isNotBlank() || selectedStatus != null)
                                "No invoices match the current filters."
                            else "No invoices created yet.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onCreateInvoice,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Create Your First Invoice")
                        }
                    }
                }
            } else {
                items(filteredInvoices, key = { it.id }) { invoice ->
                    InvoiceCard(
                        invoice = invoice,
                        onCardClick = { onSelectInvoice(invoice) },
                        onEdit = { onEditInvoice(invoice) },
                        onDuplicate = { viewModel.duplicateInvoice(invoice) },
                        onDelete = { viewModel.deleteInvoice(invoice.id) },
                        onSendEmail = { onSendEmail(invoice) }
                    )
                }
            }

            // Navigation links footer
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onNavigateHome,
                        colors = ButtonDefaults.textButtonColors()
                    ) {
                        Text("Billnest Overview", fontSize = 13.sp)
                    }
                    Text("•", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                    Button(
                        onClick = onNavigateAdmin,
                        colors = ButtonDefaults.textButtonColors()
                    ) {
                        Text("CMS Settings", fontSize = 13.sp)
                    }
                }
                Spacer(modifier = Modifier.height(64.dp))
            }
        }
    }
}
