package com.opsec.fieldintelligence.location

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.opsec.fieldintelligence.MainActivity
import com.opsec.fieldintelligence.R
import java.util.Locale

class LocationService : LifecycleService() {

    private lateinit var locationProvider: LocationProvider

    companion object {
        const val ACTION_LOCATION_UPDATE = "com.opsec.fieldintelligence.LOCATION_UPDATE"
        const val EXTRA_LAT = "lat"
        const val EXTRA_LON = "lon"
        const val EXTRA_ACCURACY = "accuracy"

        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "field_intel_location"

        fun startService(context: Context) {
            val intent = Intent(context, LocationService::class.java)
            ContextCompat.startForegroundService(context, intent)
        }

        fun stopService(context: Context) {
            context.stopService(Intent(context, LocationService::class.java))
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification("Acquiring location\u2026"))
        locationProvider = LocationProvider(this)
        locationProvider.startUpdates { location ->
            updateNotification(location.latitude, location.longitude, location.accuracy)
            broadcastLocation(location.latitude, location.longitude, location.accuracy)
        }
    }

    override fun onDestroy() {
        locationProvider.stopUpdates()
        super.onDestroy()
    }

    private fun broadcastLocation(lat: Double, lon: Double, accuracy: Float) {
        val intent = Intent(ACTION_LOCATION_UPDATE).apply {
            putExtra(EXTRA_LAT, lat)
            putExtra(EXTRA_LON, lon)
            putExtra(EXTRA_ACCURACY, accuracy)
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Location Tracking",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Field intelligence position tracking"
                setShowBadge(false)
            }
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }

    private fun buildNotification(contentText: String) =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Field Intelligence")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_my_location)
            .setOngoing(true)
            .setSilent(true)
            .setContentIntent(
                PendingIntent.getActivity(
                    this, 0,
                    Intent(this, MainActivity::class.java),
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            .build()

    private fun updateNotification(lat: Double, lon: Double, accuracy: Float) {
        val text = String.format(Locale.US, "%.5f, %.5f  ±%.0fm", lat, lon, accuracy)
        getSystemService(NotificationManager::class.java)
            .notify(NOTIFICATION_ID, buildNotification(text))
    }
}
