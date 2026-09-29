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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billnest.data.model.CustomerOnboardingAnswers

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    initialBusinessName: String,
    onComplete: (CustomerOnboardingAnswers) -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    var businessName by remember { mutableStateOf(initialBusinessName.ifBlank { "My Creative Studio" }) }
    var industry by remember { mutableStateOf("Design & Creative") }
    var businessSize by remember { mutableStateOf("Freelancer / Solo") }
    var defaultCurrency by remember { mutableStateOf("₹") }
    var paymentTerms by remember { mutableStateOf("Net 15") }
    var acceptedPayments by remember { mutableStateOf(listOf("UPI", "Bank Wire / NEFT")) }
    var primaryGoal by remember { mutableStateOf("Get paid faster") }

    val industries = listOf("Design & Creative", "Software & IT", "Marketing & Growth", "Consulting", "Photography", "Other")
    val sizes = listOf("Freelancer / Solo", "Small Agency (2-10)", "Mid Studio (11-50)")
    val currencies = listOf("₹", "$", "€", "£", "A$", "C$")
    val termsList = listOf("Due on Receipt", "Net 15", "Net 30", "Net 60")
    val paymentOptions = listOf("UPI", "Bank Wire / NEFT", "Credit Card", "PayPal", "Cash")
    val goals = listOf("Get paid faster", "Look more professional", "Track cashflow easily", "Save time billing")

    Scaffold(
        modifier = modifier.testTag("onboarding_screen"),
        topBar = {
            TopAppBar(
                title = { Text("Studio Setup", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onSkip) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Skip")
                    }
                },
                actions = {
                    OutlinedButton(onClick = onSkip, modifier = Modifier.padding(end = 8.dp)) {
                        Text("Skip")
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
            item {
                Column(modifier = Modifier.padding(bottom = 8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Welcome to Billnest!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Text("Let's personalize your billing experience in 60 seconds.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }
                    }
                }
            }

            // Step 1: Business Identity
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Business, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("1. Business Identity", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }

                        OutlinedTextField(
                            value = businessName,
                            onValueChange = { businessName = it },
                            label = { Text("Studio or Business Name") },
                            modifier = Modifier.fillMaxWidth().testTag("onboarding_business_name_input"),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )

                        Text("Industry", style = MaterialTheme.typography.labelMedium)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            industries.take(3).forEach { ind ->
                                FilterChip(
                                    selected = industry == ind,
                                    onClick = { industry = ind },
                                    label = { Text(ind, fontSize = 12.sp) }
                                )
                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            industries.drop(3).forEach { ind ->
                                FilterChip(
                                    selected = industry == ind,
                                    onClick = { industry = ind },
                                    label = { Text(ind, fontSize = 12.sp) }
                                )
                            }
                        }
                    }
                }
            }

            // Step 2: Currency & Payment Terms
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Payments, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("2. Billing Preferences", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }

                        Text("Default Currency", style = MaterialTheme.typography.labelMedium)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            currencies.forEach { cur ->
                                FilterChip(
                                    selected = defaultCurrency == cur,
                                    onClick = { defaultCurrency = cur },
                                    label = { Text(cur, fontWeight = FontWeight.Bold) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        Text("Default Payment Terms", style = MaterialTheme.typography.labelMedium)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            termsList.forEach { term ->
                                FilterChip(
                                    selected = paymentTerms == term,
                                    onClick = { paymentTerms = term },
                                    label = { Text(term, fontSize = 12.sp) }
                                )
                            }
                        }
                    }
                }
            }

            // Step 3: Accepted Payments & Goals
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
                        Text("3. Payment Methods You Accept", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                        paymentOptions.forEach { method ->
                            val isSelected = acceptedPayments.contains(method)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    acceptedPayments = if (isSelected) acceptedPayments - method else acceptedPayments + method
                                },
                                label = { Text(method) },
                                leadingIcon = {
                                    if (isSelected) Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text("4. What's your top goal with Billnest?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                        goals.forEach { goal ->
                            FilterChip(
                                selected = primaryGoal == goal,
                                onClick = { primaryGoal = goal },
                                label = { Text(goal) }
                            )
                        }
                    }
                }
            }

            // Finish Button
            item {
                Button(
                    onClick = {
                        onComplete(
                            CustomerOnboardingAnswers(
                                businessName = businessName,
                                industry = industry,
                                businessSize = businessSize,
                                defaultCurrency = defaultCurrency,
                                invoiceVolume = "1-10 / month",
                                paymentTerms = paymentTerms,
                                acceptedPayments = acceptedPayments,
                                primaryGoal = primaryGoal
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("onboarding_complete_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Done, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Complete Setup & Start Invoicing", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
