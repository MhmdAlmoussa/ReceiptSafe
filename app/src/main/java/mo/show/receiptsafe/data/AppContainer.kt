package mo.show.receiptsafe.data

import android.content.Context
import mo.show.receiptsafe.data.database.AppDatabase
import mo.show.receiptsafe.data.manager.FileStorageManager
import mo.show.receiptsafe.data.repository.ProductRepositoryImpl
import mo.show.receiptsafe.domain.repository.ProductRepository

interface AppContainer {
    val productRepository: ProductRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val productRepository: ProductRepository by lazy {
        ProductRepositoryImpl(
            productDao = AppDatabase.getDatabase(context).productDao(),
            fileStorageManager = FileStorageManager(context)
        )
    }
}
