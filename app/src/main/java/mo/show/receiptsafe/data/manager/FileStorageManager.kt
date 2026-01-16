package mo.show.receiptsafe.data.manager

import android.content.Context
import java.io.File
import java.io.InputStream
import java.util.UUID

class FileStorageManager(private val context: Context) {

    fun saveImage(inputStream: InputStream): String {
        val fileName = "receipt_${UUID.randomUUID()}.jpg"
        val file = File(context.filesDir, fileName)
        file.outputStream().use { output ->
            inputStream.copyTo(output)
        }
        return file.absolutePath
    }

    fun deleteImage(path: String) {
        val file = File(path)
        if (file.exists()) {
            file.delete()
        }
    }
    
    fun getFile(path: String): File? {
        val file = File(path)
        return if (file.exists()) file else null
    }
}
