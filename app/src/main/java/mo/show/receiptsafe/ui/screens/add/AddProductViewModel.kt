package mo.show.receiptsafe.ui.screens.add

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mo.show.receiptsafe.domain.usecase.AddProductUseCase
import java.io.InputStream

class AddProductViewModel(
    private val addProductUseCase: AddProductUseCase
) : ViewModel() {

    var name by mutableStateOf("")
    var purchaseDate by mutableStateOf(System.currentTimeMillis())
    var warrantyDuration by mutableStateOf("12") // Default 12 months
    var selectedImageUri by mutableStateOf<Uri?>(null)
    var type by mutableStateOf("RECEIPT") // Default to RECEIPT
    var price by mutableStateOf("")

    fun saveProduct(imageInputStream: InputStream?, onSuccess: () -> Unit) {
        if (!validateInput()) return

        val durationInt = warrantyDuration.toIntOrNull() ?: 0
        val priceDouble = price.toDoubleOrNull() ?: 0.0
        viewModelScope.launch {
            addProductUseCase(
                name = name,
                purchaseDate = purchaseDate,
                warrantyDurationMonths = durationInt,
                receiptImageStream = imageInputStream,
                type = type,
                price = priceDouble
            )
            onSuccess()
        }
    }

    private fun validateInput(): Boolean {
        if (name.isBlank()) return false
        if (price.toDoubleOrNull() == null || price.toDouble() <= 0) return false
        if (purchaseDate > System.currentTimeMillis()) return false
        if (type == "WARRANTY" && warrantyDuration.toIntOrNull() == null) return false
        return true
    }
}
