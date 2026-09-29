package com.example.billnest.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.billnest.data.local.BillnestDatabase
import com.example.billnest.data.model.BankDetails
import com.example.billnest.data.model.CmsContent
import com.example.billnest.data.model.CustomerOnboardingAnswers
import com.example.billnest.data.model.Invoice
import com.example.billnest.data.model.InvoiceItem
import com.example.billnest.data.model.InvoiceStatus
import com.example.billnest.data.model.UserProfile
import com.example.billnest.data.repository.InvoiceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InvoiceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: InvoiceRepository
    val allInvoices: StateFlow<List<Invoice>>

    val searchQuery = MutableStateFlow("")
    val selectedStatusFilter = MutableStateFlow<InvoiceStatus?>(null)

    val filteredInvoices: StateFlow<List<Invoice>>

    val activeInvoice = MutableStateFlow<Invoice?>(null)
    val userProfile = MutableStateFlow(UserProfile())
    val cmsContent = MutableStateFlow(CmsContent())

    init {
        val db = BillnestDatabase.getDatabase(application, viewModelScope)
        repository = InvoiceRepository(db.invoiceDao())

        allInvoices = repository.allInvoices.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        filteredInvoices = combine(allInvoices, searchQuery, selectedStatusFilter) { invoices, query, status ->
            invoices.filter { invoice ->
                val matchesQuery = query.isBlank() ||
                        invoice.clientName.contains(query, ignoreCase = true) ||
                        invoice.invoiceNumber.contains(query, ignoreCase = true) ||
                        invoice.clientEmail.contains(query, ignoreCase = true)

                val matchesStatus = status == null || invoice.status == status
                matchesQuery && matchesStatus
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun selectInvoice(invoice: Invoice) {
        activeInvoice.value = invoice
    }

    fun startNewInvoice() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = dateFormat.format(Date())
        val due = dateFormat.format(Date(System.currentTimeMillis() + 86400000L * 14))
        val currentProfile = userProfile.value

        val newInv = Invoice(
            id = "inv-" + System.currentTimeMillis(),
            invoiceNumber = "INV-" + SimpleDateFormat("yyyy", Locale.getDefault()).format(Date()) + "-" + (100..999).random(),
            businessName = currentProfile.businessName.ifBlank { "Billnest Studio" },
            businessEmail = currentProfile.email.ifBlank { "billing@billnest.app" },
            businessLogoLetter = currentProfile.businessName.firstOrNull()?.uppercase() ?: "B",
            clientName = "",
            clientEmail = "",
            issueDate = today,
            dueDate = due,
            currency = currentProfile.onboardingAnswers?.defaultCurrency ?: "₹",
            items = listOf(
                InvoiceItem(id = "item-" + System.currentTimeMillis(), description = "Consulting & Services", quantity = 1.0, rate = 1000.0)
            ),
            notes = "Thank you for your business! Payment due within 14 days.",
            bankDetails = BankDetails(
                bankName = "HDFC Bank Ltd",
                accountName = currentProfile.businessName.ifBlank { "Billnest Studio" },
                accountNumber = "50200084729103",
                routingCode = "HDFC0001234",
                upiId = "billing@billnest"
            )
        )
        activeInvoice.value = newInv
    }

    fun saveActiveInvoice(invoice: Invoice) {
        viewModelScope.launch {
            repository.saveInvoice(invoice)
            activeInvoice.value = invoice
        }
    }

    fun deleteInvoice(id: String) {
        viewModelScope.launch {
            repository.deleteInvoice(id)
            if (activeInvoice.value?.id == id) {
                activeInvoice.value = null
            }
        }
    }

    fun duplicateInvoice(invoice: Invoice) {
        viewModelScope.launch {
            val dup = repository.duplicateInvoice(invoice)
            activeInvoice.value = dup
        }
    }

    fun updateInvoiceStatus(invoice: Invoice, newStatus: InvoiceStatus) {
        val updated = invoice.copy(status = newStatus)
        saveActiveInvoice(updated)
    }

    fun completeOnboarding(answers: CustomerOnboardingAnswers) {
        val updatedProfile = userProfile.value.copy(
            businessName = answers.businessName.ifBlank { userProfile.value.businessName },
            onboardingCompleted = true,
            onboardingAnswers = answers
        )
        userProfile.value = updatedProfile

        // Update brand in CMS
        if (answers.businessName.isNotBlank()) {
            cmsContent.value = cmsContent.value.copy(
                brand = cmsContent.value.brand.copy(brandName = answers.businessName)
            )
        }
    }

    fun updateUserProfile(name: String, email: String, businessName: String, role: String) {
        userProfile.value = userProfile.value.copy(
            name = name,
            email = email,
            businessName = businessName,
            role = role
        )
    }

    fun updateCmsBrand(brandName: String, tagline: String, contactEmail: String, adminPin: String) {
        cmsContent.value = cmsContent.value.copy(
            brand = cmsContent.value.brand.copy(
                brandName = brandName,
                tagline = tagline,
                contactEmail = contactEmail,
                adminPin = adminPin
            )
        )
    }
}
