package mo.show.receiptsafe.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val purchaseDate: Long, // Epoch millis
    val warrantyDurationMonths: Int,
    val receiptImagePath: String?, // Path to internal storage
    val type: String, // "WARRANTY" or "RECEIPT"
    val createdAt: Long = System.currentTimeMillis()
)
