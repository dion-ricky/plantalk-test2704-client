package com.example.plantalk_test2704

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

private const val CHANNEL_ID = "ptnc_1"

class FirebaseService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        message.notification?.let {
            Log.d("FS_OMR", "Message body ${it.body}, title: ${it.title}")

            sendNotification(it.title, it.body)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("NEW_TOKEN", token)
    }

    fun sendNotification(title: String? = "", message: String? = "") {
        val intent = Intent(this, MainActivity::class.java)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_ONE_SHOT)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

        notificationManager.notify(notificationID, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel(notificationManager: NotificationManager) {
        val plantWatering = NotificationChannel("ptnc_1", "Plant Watering Reminder", IMPORTANCE_HIGH).apply {
            description = "Plantalk Plant Watering Notification Channel"
        }

        val promotionChannel = NotificationChannel("ptnc_2", "Promotions", IMPORTANCE_DEFAULT).apply {
            description = "Plantalk Marketing Channel"
        }

        val accountChannel = NotificationChannel("ptnc_3", "Account & Security", IMPORTANCE_HIGH).apply {
            description = "Plantalk Account and Security Channel"
        }

        notificationManager.createNotificationChannel(plantWatering)
        notificationManager.createNotificationChannel(promotionChannel)
        notificationManager.createNotificationChannel(accountChannel)
    }
}