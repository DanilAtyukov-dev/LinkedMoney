package com.danilatyukov.linkedmoney.model


import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.danilatyukov.linkedmoney.App
import com.danilatyukov.linkedmoney.R
import com.danilatyukov.linkedmoney.appComponent
import com.danilatyukov.linkedmoney.model.geolocation.GeolocationManager
import com.danilatyukov.linkedmoney.model.pedometer.PedometerManager
import javax.inject.Inject

class ForegroundService : Service() {
    @Inject
    lateinit var pedometerManager: PedometerManager

    @Inject
    lateinit var geolocationManager: GeolocationManager

    @Inject
    lateinit var foregroundServicePendingIntent: PendingIntent

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        println("service started")
        createNotificationChannel()

        App.it().appComponent.injectForegroundService(this)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("STEPS IS BENEFIT")
            .setContentText("Местоположение и шаги записываются в фоновом режиме")
            .setSmallIcon(R.drawable.notify_logo)
            .setContentIntent(foregroundServicePendingIntent)
            .build()
        startForeground(1, notification)
        initManagers()
        return START_NOT_STICKY
    }

    private fun initManagers() {
        pedometerManager.start()
        geolocationManager.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        println("service OnDestroy")
        pedometerManager.stop()
        geolocationManager.stop()
        //SavedPreference.Companion.setCurrentSteps(0);
    }

    override fun onBind(arg0: Intent): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "Foreground Service Channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = "description"
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "ForegroundServiceChannel"
    }

    init {
        onCreate()
    }
}