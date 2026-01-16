package mo.show.receiptsafe.domain.usecase

import kotlinx.coroutines.flow.Flow
import mo.show.receiptsafe.domain.model.Product
import mo.show.receiptsafe.domain.repository.ProductRepository

class GetProductsUseCase(private val repository: ProductRepository) {
    operator fun invoke(): Flow<List<Product>> {
        return repository.getAllProducts()
    }
}
