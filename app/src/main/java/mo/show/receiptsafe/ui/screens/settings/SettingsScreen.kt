package mo.show.receiptsafe.ui.screens.settings

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import mo.show.receiptsafe.ui.AppViewModelProvider

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Permission Launcher
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                viewModel.setNotificationsEnabled(true)
            } else {
                // Optionally show rationale or open settings
                viewModel.setNotificationsEnabled(false)
            }
        }
    )

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete All Data") },
            text = { Text("Are you sure you want to delete all products? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteAllData { showDeleteDialog = false }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Preferences
        SettingsSection(title = "Preferences") {
            SettingsItem(
                icon = Icons.Default.Notifications,
                title = "Notifications",
                subtitle = "Alert 10 days before expiry",
                trailing = { 
                    Switch(
                        checked = uiState.isNotificationsEnabled, 
                        onCheckedChange = { enabled ->
                            if (enabled) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    val permissionStatus = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                                    if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                                        viewModel.setNotificationsEnabled(true)
                                    } else {
                                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                } else {
                                    viewModel.setNotificationsEnabled(true)
                                }
                            } else {
                                viewModel.setNotificationsEnabled(false)
                            }
                        }
                    ) 
                }
            )
            SettingsItem(
                icon = Icons.Default.Lock,
                title = "Security",
                subtitle = "Require FaceID to open",
                trailing = { 
                    Switch(
                        checked = uiState.isSecurityEnabled, 
                        onCheckedChange = { viewModel.setSecurityEnabled(it) }
                    ) 
                }
            )
        }

        // Data Management
        SettingsSection(title = "Data Management") {
            val exportLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.CreateDocument("application/zip")
            ) { uri ->
                uri?.let { viewModel.exportData(it, onSuccess = {}, onError = {}) }
            }

            val importLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.OpenDocument()
            ) { uri ->
                uri?.let { viewModel.importData(it, onSuccess = {}, onError = {}) }
            }

            SettingsItem(
                icon = Icons.Default.Share,
                title = "Backup Data",
                subtitle = "Export your receipts",
                onClick = { exportLauncher.launch("ReceiptSafe_Backup.zip") }
            )
            SettingsItem(
                icon = Icons.Default.Share, // Using Share icon for Import temporarily or different one? Let's use something like Upload/File Download if available, or just same icon.
                title = "Restore Data",
                subtitle = "Import from backup",
                onClick = { importLauncher.launch(arrayOf("application/zip")) }
            )
            SettingsItem(
                icon = Icons.Default.Delete,
                title = "Clear All Data",
                subtitle = "Delete all products and images",
                textColor = MaterialTheme.colorScheme.error,
                onClick = { showDeleteDialog = true }
            )
        }

        // About
        SettingsSection(title = "About") {
            SettingsItem(
                icon = Icons.Default.Info,
                title = "Version",
                subtitle = "1.0.0 (Premium Build)",
                trailing = {}
            )
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
        )
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.5f))
        ) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                content()
            }
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    trailing: @Composable (() -> Unit)? = { Icon(Icons.Default.KeyboardArrowRight, null) },
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null, onClick = onClick ?: {})
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = textColor)
            if (subtitle != null) {
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        if (trailing != null) {
            trailing()
        }
    }
}
