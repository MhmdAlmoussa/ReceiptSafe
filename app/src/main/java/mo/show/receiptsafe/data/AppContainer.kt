package mo.show.receiptsafe.data

import android.content.Context
import mo.show.receiptsafe.data.database.AppDatabase
import mo.show.receiptsafe.data.manager.BackupManager
import mo.show.receiptsafe.data.manager.FileStorageManager
import mo.show.receiptsafe.data.manager.SettingsManager
import mo.show.receiptsafe.data.repository.ProductRepositoryImpl
import mo.show.receiptsafe.domain.repository.ProductRepository
import mo.show.receiptsafe.domain.usecase.*

interface AppContainer {
    val productRepository: ProductRepository
    val addProductUseCase: AddProductUseCase
    val deleteProductUseCase: DeleteProductUseCase
    val getProductsUseCase: GetProductsUseCase

    val getProductUseCase: GetProductUseCase
    val deleteAllProductsUseCase: DeleteAllProductsUseCase
    val settingsManager: SettingsManager
    val backupManager: BackupManager
    val importProductsUseCase: ImportProductsUseCase
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val productRepository: ProductRepository by lazy {
        ProductRepositoryImpl(
            productDao = AppDatabase.getDatabase(context).productDao(),
            fileStorageManager = FileStorageManager(context)
        )
    }

    override val settingsManager by lazy { SettingsManager(context) }
    override val backupManager by lazy { BackupManager(context, FileStorageManager(context)) }
    override val importProductsUseCase by lazy { ImportProductsUseCase(productRepository) }

    override val addProductUseCase by lazy { AddProductUseCase(productRepository) }
    override val deleteProductUseCase by lazy { DeleteProductUseCase(productRepository) }
    override val getProductsUseCase by lazy { GetProductsUseCase(productRepository) }
    override val getProductUseCase by lazy { GetProductUseCase(productRepository) }
    override val deleteAllProductsUseCase by lazy { DeleteAllProductsUseCase(productRepository) }
}
