package mo.show.receiptsafe

import android.app.Application
import mo.show.receiptsafe.data.AppContainer
import mo.show.receiptsafe.data.AppDataContainer

class ReceiptSafeApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
        
        // simple schedule for demo
        val request = androidx.work.PeriodicWorkRequestBuilder<mo.show.receiptsafe.worker.WarrantyCheckWorker>(
            24, java.util.concurrent.TimeUnit.HOURS
        ).build()
        
        androidx.work.WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "warranty_check",
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}
