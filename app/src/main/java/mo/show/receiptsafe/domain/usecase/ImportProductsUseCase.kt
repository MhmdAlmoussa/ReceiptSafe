package mo.show.receiptsafe.domain.usecase

import mo.show.receiptsafe.domain.model.Product
import mo.show.receiptsafe.domain.repository.ProductRepository

class ImportProductsUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(products: List<Product>) {
        repository.importProducts(products)
    }
}
