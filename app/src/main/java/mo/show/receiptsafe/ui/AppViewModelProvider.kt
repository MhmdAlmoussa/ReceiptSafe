package mo.show.receiptsafe.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import mo.show.receiptsafe.ReceiptSafeApplication
import mo.show.receiptsafe.domain.usecase.AddProductUseCase
import mo.show.receiptsafe.domain.usecase.DeleteProductUseCase
import mo.show.receiptsafe.domain.usecase.GetProductUseCase
import mo.show.receiptsafe.domain.usecase.GetProductsUseCase
import mo.show.receiptsafe.ui.screens.add.AddProductViewModel
import mo.show.receiptsafe.ui.screens.dashboard.DashboardViewModel
import mo.show.receiptsafe.ui.screens.detail.ProductDetailViewModel
import mo.show.receiptsafe.ui.screens.settings.SettingsViewModel

// will add others later

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            DashboardViewModel(
                receiptSafeApplication().container.getProductsUseCase
            )
        }
        initializer {
            AddProductViewModel(
                receiptSafeApplication().container.addProductUseCase
            )
        }
        initializer {
            ProductDetailViewModel(
                this.createSavedStateHandle(),
                receiptSafeApplication().container.getProductUseCase,
                receiptSafeApplication().container.deleteProductUseCase
            )
        }
        initializer {
            SettingsViewModel(
                deleteAllProductsUseCase = receiptSafeApplication().container.deleteAllProductsUseCase,
                settingsManager = receiptSafeApplication().container.settingsManager,
                backupManager = receiptSafeApplication().container.backupManager,
                getProductsUseCase = receiptSafeApplication().container.getProductsUseCase,
                importProductsUseCase = receiptSafeApplication().container.importProductsUseCase
            )
        }
    }
}

fun CreationExtras.receiptSafeApplication(): ReceiptSafeApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ReceiptSafeApplication)
