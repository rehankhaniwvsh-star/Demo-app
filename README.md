# Billnest — Android Invoicing & Studio Platform

**Billnest** is a modern, high-performance Android invoicing and billing management application built with **Kotlin** and **Jetpack Compose**. Engineered for freelancers, creative agencies, and independent studios to generate, customize, track, and export invoices seamlessly.

---

## 🌟 Key Features

- 📄 **Interactive Invoice Studio**: Full-featured invoice creator and editor with real-time calculations (line items, tax %, discount, subtotal, and grand total).
- 🎨 **Adaptive Themes & Templates**: Toggle between **Modern**, **Classic**, and **Minimalist** invoice templates with customizable brand accent palettes (Coral, Blue, Emerald, Slate, Purple).
- 📊 **Smart Dashboard**: Comprehensive revenue metrics (Total Billed, Total Collected, Pending Balances, Overdue tracker) and real-time status filtering (`Paid`, `Sent`, `Draft`, `Overdue`).
- 🏛️ **Local Room Database**: Offline-first persistence powered by Android Jetpack Room with Kotlin Coroutines and StateFlow.
- 🚀 **Customer Onboarding**: Interactive setup questionnaire to configure studio name, industry, default currency (₹, $, €, £, A$, C$), payment terms (Net 15/30), and payment methods (UPI, Bank Wire, Credit Card).
- 📧 **Direct Client Dispatch**: Export formatted invoice summaries or trigger direct email dispatches via Android Intent integration.
- 🔐 **PIN-Protected CMS Console**: Customize brand attributes, hero copy, and FAQ directory behind an administrator security PIN.
- 📱 **Material You & Edge-to-Edge**: Custom adaptive launcher icon with vector foreground, dynamic styling, and full edge-to-edge support.

---

## 🚀 Android Tech Stack & Architecture

- **Language**: Kotlin 2.0+
- **UI Framework**: Jetpack Compose & Material 3 (M3)
- **Architecture**: MVVM (Model-View-ViewModel) + Clean Architecture
- **State Management**: Kotlin Coroutines & `MutableStateFlow`
- **Persistence**: Room Database (`androidx.room`) with JSON TypeConverters
- **Serialization**: `kotlinx.serialization`
- **Application ID**: `com.aistudio.billnest.kxmpzq`

---

## 🛠️ Building & Running

Open the project in Android Studio or build using Gradle:

```bash
# Build Debug APK
./gradlew assembleDebug

# Run Unit Tests
./gradlew test
```

---

© Billnest • Professional Invoicing for Android
