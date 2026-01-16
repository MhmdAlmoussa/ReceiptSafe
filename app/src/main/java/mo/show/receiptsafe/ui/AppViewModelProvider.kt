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

// will add others later

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            DashboardViewModel(
                GetProductsUseCase(receiptSafeApplication().container.productRepository)
            )
        }
        initializer {
            AddProductViewModel(
                AddProductUseCase(receiptSafeApplication().container.productRepository)
            )
        }
        initializer {
            ProductDetailViewModel(
                this.createSavedStateHandle(),
                GetProductUseCase(receiptSafeApplication().container.productRepository),
                DeleteProductUseCase(receiptSafeApplication().container.productRepository)
            )
        }
    }
}

fun CreationExtras.receiptSafeApplication(): ReceiptSafeApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ReceiptSafeApplication)
