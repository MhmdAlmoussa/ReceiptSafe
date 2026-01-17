package mo.show.receiptsafe.domain.repository

import kotlinx.coroutines.flow.Flow
import mo.show.receiptsafe.domain.model.Product
import java.io.InputStream

interface ProductRepository {
    fun getAllProducts(): Flow<List<Product>>
    suspend fun getProductById(id: Int): Product?
    suspend fun insertProduct(name: String, purchaseDate: Long, warrantyDurationMonths: Int, receiptImageStream: InputStream?, type: String, price: Double): Long
    suspend fun deleteProduct(product: Product)
    suspend fun deleteAllProducts()
    suspend fun importProducts(products: List<Product>)
}
