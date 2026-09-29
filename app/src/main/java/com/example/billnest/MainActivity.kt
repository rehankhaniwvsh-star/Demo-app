package com.example.billnest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.billnest.data.model.Invoice
import com.example.billnest.data.model.InvoiceStatus
import com.example.billnest.ui.screens.AuthScreen
import com.example.billnest.ui.screens.CmsAdminScreen
import com.example.billnest.ui.screens.DashboardScreen
import com.example.billnest.ui.screens.InvoiceEditorScreen
import com.example.billnest.ui.screens.InvoicePreviewScreen
import com.example.billnest.ui.screens.LandingScreen
import com.example.billnest.ui.screens.OnboardingScreen
import com.example.billnest.ui.screens.SendEmailDialog
import com.example.billnest.ui.theme.BillnestTheme
import com.example.billnest.viewmodel.InvoiceViewModel

enum class Screen {
    Landing,
    Dashboard,
    Editor,
    Preview,
    Onboarding,
    Auth,
    CmsAdmin
}

class MainActivity : ComponentActivity() {

    private val viewModel: InvoiceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BillnestTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    BillnestApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun BillnestApp(viewModel: InvoiceViewModel) {
    var currentScreen by remember { mutableStateOf(Screen.Dashboard) }
    var emailDialogInvoice by remember { mutableStateOf<Invoice?>(null) }
    val activeInvoice by viewModel.activeInvoice.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    // Handle back button for sub-screens
    if (currentScreen != Screen.Dashboard && currentScreen != Screen.Landing) {
        BackHandler {
            currentScreen = Screen.Dashboard
        }
    } else if (currentScreen == Screen.Landing) {
        BackHandler {
            currentScreen = Screen.Dashboard
        }
    }

    when (currentScreen) {
        Screen.Landing -> {
            LandingScreen(
                viewModel = viewModel,
                onCreateInvoice = {
                    viewModel.startNewInvoice()
                    currentScreen = Screen.Editor
                },
                onNavigateDashboard = { currentScreen = Screen.Dashboard },
                onNavigateAuth = { currentScreen = Screen.Auth },
                onNavigateAdmin = { currentScreen = Screen.CmsAdmin },
                onStartOnboarding = { currentScreen = Screen.Onboarding }
            )
        }

        Screen.Dashboard -> {
            DashboardScreen(
                viewModel = viewModel,
                onCreateInvoice = {
                    viewModel.startNewInvoice()
                    currentScreen = Screen.Editor
                },
                onSelectInvoice = { inv ->
                    viewModel.selectInvoice(inv)
                    currentScreen = Screen.Preview
                },
                onEditInvoice = { inv ->
                    viewModel.selectInvoice(inv)
                    currentScreen = Screen.Editor
                },
                onSendEmail = { inv ->
                    emailDialogInvoice = inv
                },
                onNavigateHome = { currentScreen = Screen.Landing },
                onNavigateAdmin = { currentScreen = Screen.CmsAdmin },
                onNavigateAuth = { currentScreen = Screen.Auth }
            )
        }

        Screen.Editor -> {
            activeInvoice?.let { inv ->
                InvoiceEditorScreen(
                    invoice = inv,
                    onSaveInvoice = { saved ->
                        viewModel.saveActiveInvoice(saved)
                        currentScreen = Screen.Preview
                    },
                    onPreviewInvoice = { preview ->
                        viewModel.saveActiveInvoice(preview)
                        currentScreen = Screen.Preview
                    },
                    onBack = { currentScreen = Screen.Dashboard }
                )
            } ?: run {
                currentScreen = Screen.Dashboard
            }
        }

        Screen.Preview -> {
            activeInvoice?.let { inv ->
                InvoicePreviewScreen(
                    invoice = inv,
                    onBack = { currentScreen = Screen.Dashboard },
                    onEdit = { currentScreen = Screen.Editor },
                    onSendEmail = { emailDialogInvoice = inv },
                    onUpdateStatus = { newStatus ->
                        viewModel.updateInvoiceStatus(inv, newStatus)
                    }
                )
            } ?: run {
                currentScreen = Screen.Dashboard
            }
        }

        Screen.Onboarding -> {
            OnboardingScreen(
                initialBusinessName = userProfile.businessName,
                onComplete = { answers ->
                    viewModel.completeOnboarding(answers)
                    currentScreen = Screen.Dashboard
                },
                onSkip = { currentScreen = Screen.Dashboard }
            )
        }

        Screen.Auth -> {
            AuthScreen(
                viewModel = viewModel,
                onBack = { currentScreen = Screen.Dashboard },
                onStartOnboarding = { currentScreen = Screen.Onboarding }
            )
        }

        Screen.CmsAdmin -> {
            CmsAdminScreen(
                viewModel = viewModel,
                onBack = { currentScreen = Screen.Dashboard }
            )
        }
    }

    // Modal Send Email Dialog
    emailDialogInvoice?.let { inv ->
        SendEmailDialog(
            invoice = inv,
            onDismiss = { emailDialogInvoice = null },
            onSent = {
                viewModel.updateInvoiceStatus(inv, InvoiceStatus.Sent)
                emailDialogInvoice = null
            }
        )
    }
}
