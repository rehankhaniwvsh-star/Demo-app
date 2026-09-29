package com.example.billnest.data.repository

import com.example.billnest.data.local.InvoiceDao
import com.example.billnest.data.local.InvoiceEntity
import com.example.billnest.data.model.Invoice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class InvoiceRepository(private val invoiceDao: InvoiceDao) {

    val allInvoices: Flow<List<Invoice>> = invoiceDao.getAllInvoices().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun getInvoiceById(id: String): Invoice? {
        return invoiceDao.getInvoiceById(id)?.toDomain()
    }

    suspend fun saveInvoice(invoice: Invoice) {
        invoiceDao.insertInvoice(InvoiceEntity.fromDomain(invoice))
    }

    suspend fun deleteInvoice(id: String) {
        invoiceDao.deleteById(id)
    }

    suspend fun duplicateInvoice(invoice: Invoice): Invoice {
        val newInvoice = invoice.copy(
            id = "inv-" + System.currentTimeMillis(),
            invoiceNumber = invoice.invoiceNumber + "-COPY",
            status = com.example.billnest.data.model.InvoiceStatus.Draft,
            createdAt = System.currentTimeMillis()
        )
        saveInvoice(newInvoice)
        return newInvoice
    }
}
