package mo.show.receiptsafe.ui.screens.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mo.show.receiptsafe.domain.model.Product
import mo.show.receiptsafe.domain.usecase.DeleteProductUseCase
import mo.show.receiptsafe.domain.usecase.GetProductUseCase

class ProductDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val getProductUseCase: GetProductUseCase,
    private val deleteProductUseCase: DeleteProductUseCase
) : ViewModel() {

    private val productId: Int = checkNotNull(savedStateHandle["productId"]).toString().toInt()
    var product by mutableStateOf<Product?>(null)
        private set

    init {
        loadProduct()
    }

    private fun loadProduct() {
        viewModelScope.launch {
            product = getProductUseCase(productId)
        }
    }

    fun deleteProduct(onSuccess: () -> Unit) {
        viewModelScope.launch {
            product?.let {
                deleteProductUseCase(it)
                onSuccess()
            }
        }
    }
}
