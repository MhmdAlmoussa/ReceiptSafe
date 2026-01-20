package mo.show.receiptsafe.domain.usecase

import mo.show.receiptsafe.domain.repository.ProductRepository

class DeleteAllProductsUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke() {
        repository.deleteAllProducts()
    }
}
