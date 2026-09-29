package com.example.billnest.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.billnest.data.model.BankDetails
import com.example.billnest.data.model.InvoiceItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [InvoiceEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class BillnestDatabase : RoomDatabase() {
    abstract fun invoiceDao(): InvoiceDao

    companion object {
        @Volatile
        private var INSTANCE: BillnestDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): BillnestDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BillnestDatabase::class.java,
                    "billnest_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.invoiceDao())
                    }
                }
            }

            suspend fun populateInitialData(dao: InvoiceDao) {
                val initialList = listOf(
                    InvoiceEntity(
                        id = "inv-101",
                        invoiceNumber = "INV-2026-001",
                        businessName = "Billnest Studio",
                        businessEmail = "billing@billnest.app",
                        businessLogoLetter = "B",
                        clientName = "Nova Studio",
                        clientEmail = "hello@novastudio.com",
                        issueDate = "2026-07-15",
                        dueDate = "2026-07-29",
                        items = listOf(
                            InvoiceItem("item-1", "Brand Identity & Guidelines", 1.0, 25000.0),
                            InvoiceItem("item-2", "Custom Web Development", 1.0, 24000.0)
                        ),
                        taxRate = 18.0,
                        discountAmount = 1000.0,
                        notes = "Thank you for choosing Billnest for your branding project!",
                        status = "Paid",
                        currency = "₹",
                        themeColor = "#FF5238",
                        templateStyle = "Modern",
                        bankDetails = BankDetails(
                            bankName = "HDFC Bank Ltd",
                            accountName = "Billnest Studio Private Limited",
                            accountNumber = "50200084729103",
                            routingCode = "HDFC0001234",
                            iban = "IN50HDFC00012345020008472",
                            upiId = "billnest@hdfcbank",
                            paymentInstructions = "Please include Invoice #INV-2026-001 in payment reference."
                        ),
                        createdAt = System.currentTimeMillis() - 86400000L * 10
                    ),
                    InvoiceEntity(
                        id = "inv-102",
                        invoiceNumber = "INV-2026-002",
                        businessName = "Billnest Studio",
                        businessEmail = "billing@billnest.app",
                        businessLogoLetter = "B",
                        clientName = "Acme Global Corp",
                        clientEmail = "accounts@acmeglobal.io",
                        issueDate = "2026-07-20",
                        dueDate = "2026-08-04",
                        items = listOf(
                            InvoiceItem("item-3", "Q3 Product Strategy Consulting", 10.0, 3500.0),
                            InvoiceItem("item-4", "UI Design System Architecture", 1.0, 18000.0)
                        ),
                        taxRate = 18.0,
                        discountAmount = 0.0,
                        notes = "Payment due within 15 business days.",
                        status = "Sent",
                        currency = "₹",
                        themeColor = "#2563EB",
                        templateStyle = "Classic",
                        bankDetails = BankDetails(
                            bankName = "Standard Chartered Bank",
                            accountName = "Billnest Studio Global",
                            accountNumber = "987654321098",
                            routingCode = "SCBL0036001",
                            iban = "IN88SCBL0036001987654321",
                            upiId = "billnest@scb",
                            paymentInstructions = "NEFT / RTGS / Wire transfers accepted."
                        ),
                        createdAt = System.currentTimeMillis() - 86400000L * 5
                    ),
                    InvoiceEntity(
                        id = "inv-103",
                        invoiceNumber = "INV-2026-003",
                        businessName = "Billnest Studio",
                        businessEmail = "billing@billnest.app",
                        businessLogoLetter = "B",
                        clientName = "Apex Creative Lab",
                        clientEmail = "finance@apexcreative.co",
                        issueDate = "2026-07-28",
                        dueDate = "2026-08-11",
                        items = listOf(
                            InvoiceItem("item-5", "Monthly Creative Retainer - August", 1.0, 30000.0)
                        ),
                        taxRate = 0.0,
                        discountAmount = 0.0,
                        notes = "Draft invoice for upcoming design retainer.",
                        status = "Draft",
                        currency = "₹",
                        themeColor = "#10B981",
                        templateStyle = "Minimal",
                        bankDetails = BankDetails(
                            bankName = "State Bank of India",
                            accountName = "Billnest Studio LLC",
                            accountNumber = "1234876590",
                            routingCode = "SBIN0001210",
                            paymentInstructions = "IMPS / UPI transfers preferred."
                        ),
                        createdAt = System.currentTimeMillis() - 86400000L * 2
                    ),
                    InvoiceEntity(
                        id = "inv-104",
                        invoiceNumber = "INV-2026-004",
                        businessName = "Billnest Studio",
                        businessEmail = "billing@billnest.app",
                        businessLogoLetter = "B",
                        clientName = "Vanguard Tech Inc",
                        clientEmail = "payables@vanguardtech.dev",
                        issueDate = "2026-06-10",
                        dueDate = "2026-06-24",
                        items = listOf(
                            InvoiceItem("item-6", "Mobile App Wireframes & Jetpack Compose UI", 1.0, 42000.0)
                        ),
                        taxRate = 18.0,
                        discountAmount = 500.0,
                        notes = "Overdue balance notice. Please process immediately.",
                        status = "Overdue",
                        currency = "₹",
                        themeColor = "#EF4444",
                        templateStyle = "Classic",
                        bankDetails = BankDetails(
                            bankName = "HDFC Bank Ltd",
                            accountName = "Billnest Studio Private Limited",
                            accountNumber = "50200084729103",
                            routingCode = "HDFC0001234",
                            upiId = "billnest@hdfcbank"
                        ),
                        createdAt = System.currentTimeMillis() - 86400000L * 25
                    )
                )
                dao.insertAll(initialList)
            }
        }
    }
}
