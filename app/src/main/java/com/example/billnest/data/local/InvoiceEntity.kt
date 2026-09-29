package com.example.billnest.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.billnest.data.model.BankDetails
import com.example.billnest.data.model.Invoice
import com.example.billnest.data.model.InvoiceItem
import com.example.billnest.data.model.InvoiceStatus
import com.example.billnest.data.model.TemplateStyle

@Entity(tableName = "invoices")
data class InvoiceEntity(
    @PrimaryKey val id: String,
    val invoiceNumber: String,
    val businessName: String,
    val businessEmail: String,
    val businessLogoLetter: String,
    val clientName: String,
    val clientEmail: String,
    val issueDate: String,
    val dueDate: String,
    val items: List<InvoiceItem>,
    val taxRate: Double,
    val discountAmount: Double,
    val notes: String,
    val status: String,
    val currency: String,
    val themeColor: String,
    val templateStyle: String,
    val bankDetails: BankDetails,
    val createdAt: Long
) {
    fun toDomain(): Invoice {
        return Invoice(
            id = id,
            invoiceNumber = invoiceNumber,
            businessName = businessName,
            businessEmail = businessEmail,
            businessLogoLetter = businessLogoLetter,
            clientName = clientName,
            clientEmail = clientEmail,
            issueDate = issueDate,
            dueDate = dueDate,
            items = items,
            taxRate = taxRate,
            discountAmount = discountAmount,
            notes = notes,
            status = try { InvoiceStatus.valueOf(status) } catch (e: Exception) { InvoiceStatus.Draft },
            currency = currency,
            themeColor = themeColor,
            templateStyle = try { TemplateStyle.valueOf(templateStyle) } catch (e: Exception) { TemplateStyle.Modern },
            bankDetails = bankDetails,
            createdAt = createdAt
        )
    }

    companion object {
        fun fromDomain(invoice: Invoice): InvoiceEntity {
            return InvoiceEntity(
                id = invoice.id,
                invoiceNumber = invoice.invoiceNumber,
                businessName = invoice.businessName,
                businessEmail = invoice.businessEmail,
                businessLogoLetter = invoice.businessLogoLetter,
                clientName = invoice.clientName,
                clientEmail = invoice.clientEmail,
                issueDate = invoice.issueDate,
                dueDate = invoice.dueDate,
                items = invoice.items,
                taxRate = invoice.taxRate,
                discountAmount = invoice.discountAmount,
                notes = invoice.notes,
                status = invoice.status.name,
                currency = invoice.currency,
                themeColor = invoice.themeColor,
                templateStyle = invoice.templateStyle.name,
                bankDetails = invoice.bankDetails,
                createdAt = invoice.createdAt
            )
        }
    }
}
