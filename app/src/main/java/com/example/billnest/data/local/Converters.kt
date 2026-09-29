package com.example.billnest.data.local

import androidx.room.TypeConverter
import com.example.billnest.data.model.BankDetails
import com.example.billnest.data.model.InvoiceItem
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromItemList(items: List<InvoiceItem>): String {
        return json.encodeToString(items)
    }

    @TypeConverter
    fun toItemList(value: String): List<InvoiceItem> {
        return try {
            json.decodeFromString(value)
        } catch (e: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun fromBankDetails(bank: BankDetails): String {
        return json.encodeToString(bank)
    }

    @TypeConverter
    fun toBankDetails(value: String): BankDetails {
        return try {
            json.decodeFromString(value)
        } catch (e: Exception) {
            BankDetails()
        }
    }
}
