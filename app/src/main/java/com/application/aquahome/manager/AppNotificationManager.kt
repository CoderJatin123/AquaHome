package com.application.aquahome.manager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.Manifest
import android.app.Notification
import com.application.aquahome.MainActivity
import com.application.aquahome.R

class AppNotificationManager(private val cxt: Context) {
    private val NOTIFICATION_ID=101
    init{
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){

            val channel= NotificationChannel(KeyManager.NOTIFICATION_CHANNEL_ID,KeyManager.CHANNEL_NAME,KeyManager.CHANNEL_IMPORTANCE).apply {
                description=KeyManager.CHANNEL_DESCRIPTION
            }
            val notificationManager: NotificationManager= cxt.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun getNotification(title:String,content:String, details: String): Notification {

        val intent = Intent(cxt, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(cxt, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(cxt,KeyManager.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_water_drop)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(details))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setColor(cxt.getColor(R.color.md_theme_primaryFixedDim_highContrast))
            .setContentIntent(pendingIntent).build()

        return notification
    }

    fun postNotification(title: String, content: String,details: String){

        val notification = getNotification(title,content,details)
        if (ActivityCompat.checkSelfPermission(
                cxt,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        return NotificationManagerCompat.from(cxt).notify(NOTIFICATION_ID,notification)
    }
}