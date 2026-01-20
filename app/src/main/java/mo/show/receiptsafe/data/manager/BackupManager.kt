package mo.show.receiptsafe.data.manager

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mo.show.receiptsafe.domain.model.Product
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class BackupManager(
    private val context: Context,
    private val fileStorageManager: FileStorageManager
) {
    private val gson = Gson()
    private val backupJsonFileName = "backup_data.json"

    suspend fun exportData(products: List<Product>, destUri: Uri) = withContext(Dispatchers.IO) {
        context.contentResolver.openOutputStream(destUri)?.use { outputStream ->
            ZipOutputStream(BufferedOutputStream(outputStream)).use { zipOut ->
                // 1. Create and add JSON file
                val jsonString = gson.toJson(products)
                val jsonEntry = ZipEntry(backupJsonFileName)
                zipOut.putNextEntry(jsonEntry)
                zipOut.write(jsonString.toByteArray())
                zipOut.closeEntry()

                // 2. Add Images
                products.forEach { product ->
                    product.receiptImagePath?.let { imagePath ->
                        val file = fileStorageManager.getFile(imagePath)
                        if (file != null && file.exists()) {
                            val imageEntry = ZipEntry("images/${file.name}")
                            zipOut.putNextEntry(imageEntry)
                            FileInputStream(file).use { fis ->
                                fis.copyTo(zipOut)
                            }
                            zipOut.closeEntry()
                        }
                    }
                }
            }
        }
    }

    suspend fun importData(sourceUri: Uri): List<Product> = withContext(Dispatchers.IO) {
        val products = mutableListOf<Product>()
        
        context.contentResolver.openInputStream(sourceUri)?.use { inputStream ->
            ZipInputStream(BufferedInputStream(inputStream)).use { zipIn ->
                var entry: ZipEntry? = zipIn.nextEntry
                while (entry != null) {
                    if (entry.name == backupJsonFileName) {
                        val jsonString = zipIn.bufferedReader().readText()
                        val type = object : TypeToken<List<Product>>() {}.type
                        val parsedProducts: List<Product> = gson.fromJson(jsonString, type)
                        products.addAll(parsedProducts)
                    } else if (entry.name.startsWith("images/")) {
                        // Extract image
                        val fileName = File(entry.name).name
                        val outputFile = File(context.filesDir, fileName)
                        FileOutputStream(outputFile).use { fos ->
                            zipIn.copyTo(fos)
                        }
                    }
                    zipIn.closeEntry()
                    entry = zipIn.nextEntry
                }
            }
        }
        
        // Fix image paths in products (ensure they point to the new local files)
        // Since we extracted them to filesDir with valid names, we just need to ensure 
        // the path in the product object matches the absolute path of the new file.
        // The original path from JSON might be different if it was absolute.
        // We will assume the filename in the JSON path matches the filename we zipped.
        
        return@withContext products.map { product ->
            val newPath = product.receiptImagePath?.let { oldPath ->
                val fileName = File(oldPath).name
                File(context.filesDir, fileName).absolutePath
            }
            product.copy(receiptImagePath = newPath)
        }
    }
}
