package mo.show.receiptsafe.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import android.net.Uri
import kotlinx.coroutines.flow.first
import mo.show.receiptsafe.data.manager.BackupManager
import mo.show.receiptsafe.data.manager.SettingsManager
import mo.show.receiptsafe.domain.usecase.DeleteAllProductsUseCase
import mo.show.receiptsafe.domain.usecase.GetProductsUseCase
import mo.show.receiptsafe.domain.usecase.ImportProductsUseCase

data class SettingsUiState(
    val isNotificationsEnabled: Boolean = false,
    val isSecurityEnabled: Boolean = false
)

class SettingsViewModel(
    private val deleteAllProductsUseCase: DeleteAllProductsUseCase,
    private val settingsManager: SettingsManager,
    private val backupManager: BackupManager,
    private val getProductsUseCase: GetProductsUseCase,
    private val importProductsUseCase: ImportProductsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        _uiState.update {
            it.copy(
                isNotificationsEnabled = settingsManager.isNotificationsEnabled,
                isSecurityEnabled = settingsManager.isSecurityEnabled
            )
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        settingsManager.isNotificationsEnabled = enabled
        loadSettings() 
    }

    fun setSecurityEnabled(enabled: Boolean) {
        settingsManager.isSecurityEnabled = enabled
        loadSettings()
    }

    fun exportData(destUri: Uri, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val products = getProductsUseCase().first()
                backupManager.exportData(products, destUri)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Export failed")
            }
        }
    }

    fun importData(sourceUri: Uri, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val products = backupManager.importData(sourceUri)
                importProductsUseCase(products)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Import failed")
            }
        }
    }

    fun deleteAllData(onSuccess: () -> Unit) {
        viewModelScope.launch {
            deleteAllProductsUseCase()
            onSuccess()
        }
    }
}
