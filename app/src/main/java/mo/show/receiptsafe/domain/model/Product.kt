package mo.show.receiptsafe.domain.model

data class Product(
    val id: Int = 0,
    val name: String,
    val purchaseDate: Long,
    val warrantyDurationMonths: Int,
    val receiptImagePath: String?,
    val type: String, // "WARRANTY" or "RECEIPT"
    val isExpired: Boolean
)
