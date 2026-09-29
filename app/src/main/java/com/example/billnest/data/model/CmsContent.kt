package com.example.billnest.data.model

import kotlinx.serialization.Serializable

@Serializable
data class BrandSettings(
    val brandName: String = "Billnest",
    val tagline: String = "Invoices, paid faster",
    val primaryColor: String = "#FF5238",
    val accentColor: String = "#FF7A00",
    val contactEmail: String = "support@billnest.app",
    val adminPin: String = "1234"
)

@Serializable
data class FaqItem(
    val id: String,
    val question: String,
    val answer: String,
    val category: String = "General"
)

@Serializable
data class TestimonialItem(
    val id: String,
    val author: String,
    val role: String,
    val company: String,
    val quote: String,
    val rating: Int = 5
)

@Serializable
data class FeatureItem(
    val id: String,
    val title: String,
    val description: String,
    val iconTag: String
)

@Serializable
data class CmsContent(
    val brand: BrandSettings = BrandSettings(),
    val heroHeadline: String = "Invoices that get you paid faster.",
    val heroSubheadline: String = "The intuitive invoicing suite built for modern freelancers and ambitious studios. Craft professional invoices, accept payments, and streamline your billing in seconds.",
    val features: List<FeatureItem> = listOf(
        FeatureItem("f1", "Instant Invoice Generation", "Build polished, branded invoices in under 60 seconds with live preview.", "bolt"),
        FeatureItem("f2", "Multi-Currency & UPI", "Bill globally in ₹ INR, $ USD, € EUR, £ GBP with instant UPI QR code support.", "currency"),
        FeatureItem("f3", "Custom Studio Branding", "Select your color schemes, signature fonts, and modern invoice templates.", "palette"),
        FeatureItem("f4", "One-Tap Client Sharing", "Direct email dispatch, clean PDF export, and payment tracking status.", "share")
    ),
    val faqs: List<FaqItem> = listOf(
        FaqItem("q1", "How do I create and send my first invoice?", "Click 'Create Invoice' from the dashboard, fill in your client details and line items, customize your color and payment terms, and hit Export or Send to Client.", "Getting Started"),
        FaqItem("q2", "Does Billnest support UPI and Direct Wire?", "Yes! You can specify your UPI VPA, Bank Account, IFSC, SWIFT/BIC, and IBAN details which automatically render on the invoice.", "Payments"),
        FaqItem("q3", "Can I manage multiple invoice templates?", "Absolutely. You can toggle between Modern, Classic, and Minimalist templates at any time.", "Templates"),
        FaqItem("q4", "Is my invoice data stored securely offline?", "Yes, all your invoices and profile data are stored directly on your device using a local Room SQLite database.", "Security")
    ),
    val testimonials: List<TestimonialItem> = listOf(
        TestimonialItem("t1", "Arjun Mehta", "Creative Director", "Nova Design Labs", "Billnest made our retainer billing effortless. Our clients love the clean design and payment clarity.", 5),
        TestimonialItem("t2", "Sarah Jenkins", "Full-Stack Consultant", "Apex Software", "The fastest invoice creator I've ever used on Android. Simple, blazing fast, and zero clutter.", 5)
    )
)
