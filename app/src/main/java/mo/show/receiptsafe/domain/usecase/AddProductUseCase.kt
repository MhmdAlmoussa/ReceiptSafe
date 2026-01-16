package mo.show.receiptsafe.domain.usecase

import mo.show.receiptsafe.domain.repository.ProductRepository
import java.io.InputStream

class AddProductUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(
        name: String,
        purchaseDate: Long,
        warrantyDurationMonths: Int,
        receiptImageStream: InputStream?,
        type: String
    ) {
        repository.insertProduct(name, purchaseDate, warrantyDurationMonths, receiptImageStream, type)
    }
}
