package mo.show.receiptsafe.domain.usecase

import mo.show.receiptsafe.domain.model.Product
import mo.show.receiptsafe.domain.repository.ProductRepository

class DeleteProductUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(product: Product) {
        repository.deleteProduct(product)
    }
}
