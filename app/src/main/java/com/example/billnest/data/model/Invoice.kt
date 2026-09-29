package com.example.billnest.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class InvoiceStatus {
    Paid, Sent, Draft, Overdue
}

@Serializable
enum class TemplateStyle {
    Modern, Classic, Minimal
}

@Serializable
data class InvoiceItem(
    val id: String,
    val description: String,
    val quantity: Double,
    val rate: Double
) {
    val amount: Double get() = quantity * rate
}

@Serializable
data class BankDetails(
    val bankName: String = "",
    val accountName: String = "",
    val accountNumber: String = "",
    val routingCode: String = "",
    val iban: String = "",
    val upiId: String = "",
    val paymentInstructions: String = ""
)

@Serializable
data class Invoice(
    val id: String,
    val invoiceNumber: String,
    val businessName: String = "Billnest Studio",
    val businessEmail: String = "billing@billnest.app",
    val businessLogoLetter: String = "B",
    val clientName: String,
    val clientEmail: String,
    val issueDate: String,
    val dueDate: String,
    val items: List<InvoiceItem> = emptyList(),
    val taxRate: Double = 0.0,
    val discountAmount: Double = 0.0,
    val notes: String = "",
    val status: InvoiceStatus = InvoiceStatus.Draft,
    val currency: String = "₹",
    val themeColor: String = "#FF5238",
    val templateStyle: TemplateStyle = TemplateStyle.Modern,
    val bankDetails: BankDetails = BankDetails(),
    val createdAt: Long = System.currentTimeMillis()
) {
    val subtotal: Double get() = items.sumOf { it.amount }
    val taxAmount: Double get() = (subtotal * taxRate) / 100.0
    val total: Double get() = maxOf(0.0, subtotal + taxAmount - discountAmount)
}
