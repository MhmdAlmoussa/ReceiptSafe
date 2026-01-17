package mo.show.receiptsafe.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.first
import mo.show.receiptsafe.R
import mo.show.receiptsafe.ReceiptSafeApplication
import java.util.Calendar

class WarrantyCheckWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val application = applicationContext as ReceiptSafeApplication
        val repository = application.container.productRepository
        val settingsManager = application.container.settingsManager

        // Check if notifications are enabled
        if (!settingsManager.isNotificationsEnabled) {
            return Result.success()
        }

        // Get all products
        val products = repository.getAllProducts().first()
        val today = System.currentTimeMillis()
        
        // Notify for warranties expiring in the next 10 days
        val next10Days = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 10) }.timeInMillis

        val expiringSoon = products.filter { product ->
            if (product.isExpired) return@filter false
            
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = product.purchaseDate
            calendar.add(Calendar.MONTH, product.warrantyDurationMonths)
            val expiryDate = calendar.timeInMillis
            
            expiryDate > today && expiryDate <= next10Days
        }

        if (expiringSoon.isNotEmpty()) {
            sendNotification(expiringSoon.size)
        }

        return Result.success()
    }

    private fun sendNotification(count: Int) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "warranty_expiry_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Warranty Expiry",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Warranty Alert")
            .setContentText("$count items are expiring soon!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(1, notification)
    }
}
