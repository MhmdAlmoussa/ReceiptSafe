package mo.show.receiptsafe.domain.usecase

import mo.show.receiptsafe.domain.model.Product
import mo.show.receiptsafe.domain.repository.ProductRepository

class GetProductUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(id: Int): Product? {
        return repository.getProductById(id)
    }
}
