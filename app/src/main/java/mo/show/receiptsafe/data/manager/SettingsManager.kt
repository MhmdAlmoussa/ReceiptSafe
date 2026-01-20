package mo.show.receiptsafe.data.manager

import android.content.Context
import android.content.SharedPreferences

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("receiptsafe_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_SECURITY_ENABLED = "security_enabled"
    }

    var isNotificationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, value).apply()

    var isSecurityEnabled: Boolean
        get() = prefs.getBoolean(KEY_SECURITY_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_SECURITY_ENABLED, value).apply()
}
