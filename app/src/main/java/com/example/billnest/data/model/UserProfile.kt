package com.example.billnest.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CustomerOnboardingAnswers(
    val businessName: String = "",
    val industry: String = "",
    val businessSize: String = "",
    val defaultCurrency: String = "₹",
    val invoiceVolume: String = "",
    val paymentTerms: String = "Net 15",
    val acceptedPayments: List<String> = emptyList(),
    val primaryGoal: String = "",
    val notesOrTaxInfo: String = ""
)

@Serializable
data class UserProfile(
    val id: String = "user_1",
    val name: String = "Freelancer",
    val email: String = "user@billnest.app",
    val businessName: String = "Billnest Studio",
    val role: String = "Owner",
    val onboardingCompleted: Boolean = false,
    val onboardingAnswers: CustomerOnboardingAnswers? = null
)
