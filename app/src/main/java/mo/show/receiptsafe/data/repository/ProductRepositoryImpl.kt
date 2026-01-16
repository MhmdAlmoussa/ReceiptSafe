package mo.show.receiptsafe.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mo.show.receiptsafe.data.database.ProductDao
import mo.show.receiptsafe.data.database.ProductEntity
import mo.show.receiptsafe.data.manager.FileStorageManager
import mo.show.receiptsafe.domain.model.Product
import mo.show.receiptsafe.domain.repository.ProductRepository
import java.io.InputStream
import java.util.Calendar

class ProductRepositoryImpl(
    private val productDao: ProductDao,
    private val fileStorageManager: FileStorageManager
) : ProductRepository {

    override fun getAllProducts(): Flow<List<Product>> {
        return productDao.getAllProducts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getProductById(id: Int): Product? {
        return productDao.getProductById(id)?.toDomain()
    }

    override suspend fun insertProduct(
        name: String,
        purchaseDate: Long,
        warrantyDurationMonths: Int,
        receiptImageStream: InputStream?,
        type: String,
        price: Double
    ): Long {
        val imagePath = receiptImageStream?.let { fileStorageManager.saveImage(it) }
        val entity = ProductEntity(
            name = name,
            purchaseDate = purchaseDate,
            warrantyDurationMonths = warrantyDurationMonths,
            receiptImagePath = imagePath,
            type = type,
            price = price
        )
        productDao.insertProduct(entity)
        return 0L 
    }

    override suspend fun deleteProduct(product: Product) {
        product.receiptImagePath?.let { fileStorageManager.deleteImage(it) }
        val entity = ProductEntity(
            id = product.id,
            name = product.name,
            purchaseDate = product.purchaseDate,
            warrantyDurationMonths = product.warrantyDurationMonths,
            receiptImagePath = product.receiptImagePath,
            type = product.type,
            price = product.price
        )
        productDao.deleteProduct(entity)
    }

    private fun ProductEntity.toDomain(): Product {
        // Calculate expiry
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = purchaseDate
        calendar.add(Calendar.MONTH, warrantyDurationMonths)
        val expiryDate = calendar.timeInMillis
        val isExpired = System.currentTimeMillis() > expiryDate

        return Product(
            id = id,
            name = name,
            purchaseDate = purchaseDate,
            warrantyDurationMonths = warrantyDurationMonths,
            receiptImagePath = receiptImagePath,
            type = type,
            price = price,
            isExpired = isExpired
        )
    }
}
